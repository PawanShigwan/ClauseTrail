package com.clausetrail.service;

import com.clausetrail.dto.*;
import com.clausetrail.model.*;
import com.clausetrail.repository.ContractRepository;
import com.clausetrail.repository.ContractVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractVersionService {

    private static final Logger log = LoggerFactory.getLogger(ContractVersionService.class);

    private final ContractRepository contractRepository;
    private final ContractVersionRepository contractVersionRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final DiffService diffService;
    private final DocumentParserService documentParserService;
    private final UserService userService;

    public ContractVersionService(ContractRepository contractRepository, ContractVersionRepository contractVersionRepository, AuthService authService, AuditService auditService, NotificationService notificationService, DiffService diffService, DocumentParserService documentParserService, UserService userService) {
        this.contractRepository = contractRepository;
        this.contractVersionRepository = contractVersionRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.diffService = diffService;
        this.documentParserService = documentParserService;
        this.userService = userService;
    }

    public ContractResponse submitModification(String contractId, ModifyContractRequest request, String ipAddress) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        if (contract.getStatus() == ContractStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot modify an archived contract");
        }

        User currentUser = authService.getCurrentUser();

        // 1. Fetch current active version as parent
        ContractVersion parentVersion = contractVersionRepository.findById(contract.getCurrentVersionId())
                .orElseThrow(() -> new IllegalStateException("Active version not found for contract: " + contractId));

        int newVersionNumber = contract.getCurrentVersionNumber() + 1;

        // 2. Build list of Clauses
        List<Clause> newClauses = new ArrayList<>();
        StringBuilder fullTextBuilder = new StringBuilder();

        if (request.getClauses() != null && !request.getClauses().isEmpty()) {
            int idx = 1;
            for (ClauseDTO c : request.getClauses()) {
                String clauseId = c.getId() != null ? c.getId() : "clause-" + UUID.randomUUID().toString().substring(0, 8);
                newClauses.add(Clause.builder()
                        .id(clauseId)
                        .orderIndex(idx)
                        .clauseNumber(c.getClauseNumber() != null ? c.getClauseNumber() : String.valueOf(idx))
                        .title(c.getTitle())
                        .content(c.getContent())
                        .category(c.getCategory() != null ? c.getCategory() : "General")
                        .build());
                fullTextBuilder.append(c.getTitle()).append("\n").append(c.getContent()).append("\n\n");
                idx++;
            }
        } else if (request.getFullText() != null && !request.getFullText().isBlank()) {
            List<ClauseDTO> parsed = documentParserService.segmentIntoClauses(request.getFullText());
            for (ClauseDTO c : parsed) {
                newClauses.add(Clause.builder()
                        .id(c.getId())
                        .orderIndex(c.getOrderIndex())
                        .clauseNumber(c.getClauseNumber())
                        .title(c.getTitle())
                        .content(c.getContent())
                        .category(c.getCategory())
                        .build());
            }
            fullTextBuilder.append(request.getFullText());
        }

        // 3. Compute clause changes if not explicitly provided
        List<ClauseChange> changes = new ArrayList<>();
        if (request.getClauseChanges() != null && !request.getClauseChanges().isEmpty()) {
            for (ClauseChangeDTO cc : request.getClauseChanges()) {
                changes.add(ClauseChange.builder()
                        .clauseId(cc.getClauseId())
                        .clauseNumber(cc.getClauseNumber())
                        .clauseTitle(cc.getClauseTitle())
                        .previousText(cc.getPreviousText())
                        .modifiedText(cc.getModifiedText())
                        .changeType(cc.getChangeType())
                        .build());
            }
        } else {
            // Auto detect from parent version
            Map<String, Clause> parentMap = new HashMap<>();
            for (Clause pc : parentVersion.getClauses()) {
                parentMap.put(pc.getId() != null ? pc.getId() : pc.getClauseNumber(), pc);
            }

            for (Clause nc : newClauses) {
                String key = nc.getId() != null ? nc.getId() : nc.getClauseNumber();
                Clause pc = parentMap.remove(key);
                if (pc == null) {
                    changes.add(ClauseChange.builder()
                            .clauseId(nc.getId())
                            .clauseNumber(nc.getClauseNumber())
                            .clauseTitle(nc.getTitle())
                            .previousText("")
                            .modifiedText(nc.getContent())
                            .changeType(ChangeType.ADDED)
                            .build());
                } else if (!Objects.equals(pc.getContent(), nc.getContent()) || !Objects.equals(pc.getTitle(), nc.getTitle())) {
                    changes.add(ClauseChange.builder()
                            .clauseId(nc.getId())
                            .clauseNumber(nc.getClauseNumber())
                            .clauseTitle(nc.getTitle())
                            .previousText(pc.getContent())
                            .modifiedText(nc.getContent())
                            .changeType(ChangeType.MODIFIED)
                            .build());
                }
            }

            for (Clause deleted : parentMap.values()) {
                changes.add(ClauseChange.builder()
                        .clauseId(deleted.getId())
                        .clauseNumber(deleted.getClauseNumber())
                        .clauseTitle(deleted.getTitle())
                        .previousText(deleted.getContent())
                        .modifiedText("")
                        .changeType(ChangeType.DELETED)
                        .build());
            }
        }

        // 4. Create new version in PENDING_REVIEW
        ContractVersion newVersion = ContractVersion.builder()
                .contractId(contract.getId())
                .versionNumber(newVersionNumber)
                .fullText(fullTextBuilder.toString())
                .clauses(newClauses)
                .clauseChanges(changes)
                .modifiedBy(currentUser.toReference())
                .modificationReason(request.getModificationReason())
                .status(VersionStatus.PENDING_REVIEW)
                .parentVersionId(parentVersion.getId())
                .parentVersionNumber(parentVersion.getVersionNumber())
                .createdAt(Instant.now())
                .build();

        newVersion = contractVersionRepository.save(newVersion);

        // 5. Update Contract status to PENDING_REVIEW with pointer
        contract.setStatus(ContractStatus.PENDING_REVIEW);
        contract.setPendingVersionNumber(newVersionNumber);
        contract.setPendingVersionId(newVersion.getId());
        contract.setLastModifiedBy(currentUser.toReference());
        contract.setUpdatedAt(Instant.now());
        contract = contractRepository.save(contract);

        // 6. Audit Log
        auditService.logAction(
                contract.getId(),
                contract.getTitle(),
                currentUser,
                AuditAction.SUBMIT_FOR_REVIEW,
                String.format("Submitted version %d for review. Reason: %s", newVersionNumber, request.getModificationReason()),
                newVersionNumber,
                ipAddress
        );

        // 7. Notify Reviewers & Admins
        for (User reviewer : userService.getReviewersAndAdmins()) {
            if (!reviewer.getId().equals(currentUser.getId())) {
                notificationService.notifyUser(
                        reviewer.getId(),
                        "Contract Modification Pending Review",
                        String.format("Version %d of '%s' was submitted for review by %s. Reason: %s",
                                newVersionNumber, contract.getTitle(), currentUser.getName(), request.getModificationReason()),
                        NotificationType.MODIFICATION_SUBMITTED,
                        contract.getId(),
                        contract.getTitle(),
                        newVersionNumber
                );
            }
        }

        return toContractResponse(contract, true);
    }

    public List<VersionResponse> getVersionHistory(String contractId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "versionNumber");
        return contractVersionRepository.findByContractId(contractId, sort).stream()
                .map(this::toVersionResponse)
                .collect(Collectors.toList());
    }

    public VersionResponse getVersionByNumber(String contractId, int versionNumber) {
        ContractVersion version = contractVersionRepository.findByContractIdAndVersionNumber(contractId, versionNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Version %d not found for contract %s", versionNumber, contractId)));
        return toVersionResponse(version);
    }

    public DiffResponse getDiffBetweenVersions(String contractId, int v1Number, int v2Number) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        ContractVersion v1 = contractVersionRepository.findByContractIdAndVersionNumber(contractId, v1Number)
                .orElseThrow(() -> new IllegalArgumentException("Version " + v1Number + " not found for contract: " + contractId));

        ContractVersion v2 = contractVersionRepository.findByContractIdAndVersionNumber(contractId, v2Number)
                .orElseThrow(() -> new IllegalArgumentException("Version " + v2Number + " not found for contract: " + contractId));

        return diffService.computeDiff(
                contract.getId(),
                contract.getTitle(),
                toVersionResponse(v1),
                toVersionResponse(v2)
        );
    }

    public byte[] exportVersionPdf(String contractId, Integer versionNumber) throws Exception {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        int targetVersion = versionNumber != null ? versionNumber : contract.getCurrentVersionNumber();
        ContractVersion version = contractVersionRepository.findByContractIdAndVersionNumber(contractId, targetVersion)
                .orElseThrow(() -> new IllegalArgumentException("Version " + targetVersion + " not found"));

        User currentUser = authService.getCurrentUser();
        auditService.logAction(
                contract.getId(),
                contract.getTitle(),
                currentUser,
                AuditAction.EXPORT_PDF,
                "Exported PDF for version " + targetVersion,
                targetVersion,
                null
        );

        return documentParserService.generateContractPdf(contract, version);
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
