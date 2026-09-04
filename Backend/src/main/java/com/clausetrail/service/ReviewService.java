package com.clausetrail.service;

import com.clausetrail.dto.ContractResponse;
import com.clausetrail.dto.ReviewQueueItemDTO;
import com.clausetrail.dto.ReviewRequest;
import com.clausetrail.dto.VersionResponse;
import com.clausetrail.model.*;
import com.clausetrail.repository.ContractRepository;
import com.clausetrail.repository.ContractVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ContractRepository contractRepository;
    private final ContractVersionRepository contractVersionRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public ReviewService(ContractRepository contractRepository, ContractVersionRepository contractVersionRepository, AuthService authService, AuditService auditService, NotificationService notificationService) {
        this.contractRepository = contractRepository;
        this.contractVersionRepository = contractVersionRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    public List<ReviewQueueItemDTO> getReviewQueue() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<ContractVersion> pendingVersions = contractVersionRepository.findByStatus(VersionStatus.PENDING_REVIEW, sort);
        List<ReviewQueueItemDTO> queue = new ArrayList<>();

        for (ContractVersion v : pendingVersions) {
            contractRepository.findById(v.getContractId()).ifPresent(contract -> {
                queue.add(ReviewQueueItemDTO.builder()
                        .contractId(contract.getId())
                        .contractTitle(contract.getTitle())
                        .contractType(contract.getContractType())
                        .parties(contract.getParties())
                        .versionId(v.getId())
                        .versionNumber(v.getVersionNumber())
                        .parentVersionNumber(v.getParentVersionNumber() != null ? v.getParentVersionNumber() : 1)
                        .modificationReason(v.getModificationReason())
                        .modifiedBy(v.getModifiedBy())
                        .submittedAt(v.getCreatedAt())
                        .clausesChangedCount(v.getClauseChanges() != null ? v.getClauseChanges().size() : 0)
                        .changesPreview(v.getClauseChanges())
                        .build());
            });
        }

        return queue;
    }

    public ContractResponse reviewModification(String contractId, ReviewRequest request, String ipAddress) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        if (contract.getPendingVersionId() == null) {
            throw new IllegalStateException("No pending version found for contract: " + contractId);
        }

        final String pendingVersionId = contract.getPendingVersionId();
        ContractVersion pendingVersion = contractVersionRepository.findById(pendingVersionId)
                .orElseThrow(() -> new IllegalStateException("Pending version entity not found: " + pendingVersionId));

        User currentUser = authService.getCurrentUser();
        Instant now = Instant.now();

        pendingVersion.setReviewedBy(currentUser.toReference());
        pendingVersion.setReviewedAt(now);
        pendingVersion.setReviewComments(request.getReviewComments());

        if (request.getAction() == VersionStatus.APPROVED) {
            // Approve workflow
            pendingVersion.setStatus(VersionStatus.APPROVED);
            contractVersionRepository.save(pendingVersion);

            // Promote pending version to current active version
            contract.setCurrentVersionNumber(pendingVersion.getVersionNumber());
            contract.setCurrentVersionId(pendingVersion.getId());
            contract.setPendingVersionNumber(null);
            contract.setPendingVersionId(null);
            contract.setStatus(ContractStatus.APPROVED);
            contract.setLastModifiedBy(currentUser.toReference());
            contract.setUpdatedAt(now);
            contract = contractRepository.save(contract);

            // Audit
            auditService.logAction(
                    contract.getId(),
                    contract.getTitle(),
                    currentUser,
                    AuditAction.APPROVE_MODIFICATION,
                    String.format("Approved modification to version %d. Comments: %s",
                            pendingVersion.getVersionNumber(),
                            request.getReviewComments() != null ? request.getReviewComments() : "No comments"),
                    pendingVersion.getVersionNumber(),
                    ipAddress
            );

            // Notify Editor
            if (pendingVersion.getModifiedBy() != null && pendingVersion.getModifiedBy().getId() != null) {
                notificationService.notifyUser(
                        pendingVersion.getModifiedBy().getId(),
                        "Modification Approved",
                        String.format("Your submitted changes for '%s' (v%d) were approved by %s.",
                                contract.getTitle(), pendingVersion.getVersionNumber(), currentUser.getName()),
                        NotificationType.MODIFICATION_APPROVED,
                        contract.getId(),
                        contract.getTitle(),
                        pendingVersion.getVersionNumber()
                );
            }

        } else if (request.getAction() == VersionStatus.REJECTED) {
            // Reject workflow
            pendingVersion.setStatus(VersionStatus.REJECTED);
            contractVersionRepository.save(pendingVersion);

            // Clear pending pointer on contract, keeping previous active version active
            contract.setPendingVersionNumber(null);
            contract.setPendingVersionId(null);
            contract.setStatus(ContractStatus.APPROVED);
            contract.setLastModifiedBy(currentUser.toReference());
            contract.setUpdatedAt(now);
            contract = contractRepository.save(contract);

            // Audit
            auditService.logAction(
                    contract.getId(),
                    contract.getTitle(),
                    currentUser,
                    AuditAction.REJECT_MODIFICATION,
                    String.format("Rejected modification to version %d. Comments: %s",
                            pendingVersion.getVersionNumber(),
                            request.getReviewComments() != null ? request.getReviewComments() : "No comments"),
                    pendingVersion.getVersionNumber(),
                    ipAddress
            );

            // Notify Editor
            if (pendingVersion.getModifiedBy() != null && pendingVersion.getModifiedBy().getId() != null) {
                notificationService.notifyUser(
                        pendingVersion.getModifiedBy().getId(),
                        "Modification Rejected",
                        String.format("Your submitted changes for '%s' (v%d) were rejected by %s. Comments: %s",
                                contract.getTitle(), pendingVersion.getVersionNumber(), currentUser.getName(),
                                request.getReviewComments() != null ? request.getReviewComments() : "N/A"),
                        NotificationType.MODIFICATION_REJECTED,
                        contract.getId(),
                        contract.getTitle(),
                        pendingVersion.getVersionNumber()
                );
            }
        }

        return toContractResponse(contract, true);
    }

    private ContractResponse toContractResponse(Contract c, boolean includeFullVersions) {
        ContractResponse.Builder builder = ContractResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .contractType(c.getContractType())
                .parties(c.getParties())
                .description(c.getDescription())
                .tags(c.getTags())
                .currentVersionNumber(c.getCurrentVersionNumber())
                .currentVersionId(c.getCurrentVersionId())
                .pendingVersionNumber(c.getPendingVersionNumber())
                .pendingVersionId(c.getPendingVersionId())
                .status(c.getStatus())
                .createdBy(c.getCreatedBy())
                .lastModifiedBy(c.getLastModifiedBy())
                .effectiveDate(c.getEffectiveDate())
                .expirationDate(c.getExpirationDate())
                .contractValue(c.getContractValue())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt());

        if (includeFullVersions) {
            if (c.getCurrentVersionId() != null) {
                contractVersionRepository.findById(c.getCurrentVersionId())
                        .ifPresent(v -> builder.activeVersion(toVersionResponse(v)));
            }
            if (c.getPendingVersionId() != null) {
                contractVersionRepository.findById(c.getPendingVersionId())
                        .ifPresent(v -> builder.pendingVersion(toVersionResponse(v)));
            }
        }

        return builder.build();
    }

    public VersionResponse toVersionResponse(ContractVersion v) {
        return VersionResponse.builder()
                .id(v.getId())
                .contractId(v.getContractId())
                .versionNumber(v.getVersionNumber())
                .fullText(v.getFullText())
                .clauses(v.getClauses())
                .clauseChanges(v.getClauseChanges())
                .modifiedBy(v.getModifiedBy())
                .modificationReason(v.getModificationReason())
                .status(v.getStatus())
                .reviewedBy(v.getReviewedBy())
                .reviewComments(v.getReviewComments())
                .reviewedAt(v.getReviewedAt())
                .parentVersionId(v.getParentVersionId())
                .parentVersionNumber(v.getParentVersionNumber())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
