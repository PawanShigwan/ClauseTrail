package com.clausetrail.service;

import com.clausetrail.dto.*;
import com.clausetrail.model.*;
import com.clausetrail.repository.ContractRepository;
import com.clausetrail.repository.ContractVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final ContractVersionRepository contractVersionRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final DocumentParserService documentParserService;
    private final UserService userService;

    public ContractService(ContractRepository contractRepository, ContractVersionRepository contractVersionRepository, AuthService authService, AuditService auditService, NotificationService notificationService, DocumentParserService documentParserService, UserService userService) {
        this.contractRepository = contractRepository;
        this.contractVersionRepository = contractVersionRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.documentParserService = documentParserService;
        this.userService = userService;
    }

    public List<ContractResponse> getAllContracts(String search, String status, String contractType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<Contract> list;

        if (search != null && !search.isBlank()) {
            list = contractRepository.searchContracts(search, sort);
        } else if (status != null && !status.isBlank()) {
            try {
                ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
                list = contractRepository.findByStatus(contractStatus, sort);
            } catch (IllegalArgumentException e) {
                list = contractRepository.findAll(sort);
            }
        } else if (contractType != null && !contractType.isBlank()) {
            list = contractRepository.findByContractType(contractType, sort);
        } else {
            list = contractRepository.findAll(sort);
        }

        return list.stream().map(c -> toResponse(c, false)).collect(Collectors.toList());
    }

    public ContractResponse getContractById(String id, boolean includeFullVersions) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));

        User currentUser = authService.getCurrentUser();
        auditService.logAction(
                contract.getId(),
                contract.getTitle(),
                currentUser,
                AuditAction.VIEW_CONTRACT,
                "Viewed contract details",
                contract.getCurrentVersionNumber(),
                null
        );

        return toResponse(contract, includeFullVersions);
    }

    public ContractResponse createContract(CreateContractRequest request, String ipAddress) {
        User currentUser = authService.getCurrentUser();

        // 1. Convert Clauses
        List<Clause> clauses = new ArrayList<>();
        StringBuilder fullTextBuilder = new StringBuilder();

        if (request.getClauses() != null && !request.getClauses().isEmpty()) {
            int idx = 1;
            for (ClauseDTO c : request.getClauses()) {
                String clauseId = c.getId() != null ? c.getId() : "clause-" + UUID.randomUUID().toString().substring(0, 8);
                clauses.add(Clause.builder()
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
            List<ClauseDTO> parsedClauses = documentParserService.segmentIntoClauses(request.getFullText());
            for (ClauseDTO c : parsedClauses) {
                clauses.add(Clause.builder()
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

        // 2. Create Master Contract (Status = APPROVED/ACTIVE for baseline version 1)
        Contract contract = Contract.builder()
                .title(request.getTitle())
                .contractType(request.getContractType())
                .parties(request.getParties())
                .description(request.getDescription())
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .currentVersionNumber(1)
                .status(ContractStatus.APPROVED)
                .createdBy(currentUser.toReference())
                .lastModifiedBy(currentUser.toReference())
                .effectiveDate(request.getEffectiveDate())
                .expirationDate(request.getExpirationDate())
                .contractValue(request.getContractValue())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        contract = contractRepository.save(contract);

        // 3. Create Version 1.0 (Status = APPROVED baseline)
        ContractVersion version1 = ContractVersion.builder()
                .contractId(contract.getId())
                .versionNumber(1)
                .fullText(fullTextBuilder.toString())
                .clauses(clauses)
                .clauseChanges(new ArrayList<>())
                .modifiedBy(currentUser.toReference())
                .modificationReason("Initial contract creation and upload (v1.0 Baseline)")
                .status(VersionStatus.APPROVED)
                .reviewedBy(currentUser.toReference())
                .reviewComments("Initial contract baseline established")
                .reviewedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        version1 = contractVersionRepository.save(version1);

        contract.setCurrentVersionId(version1.getId());
        contract = contractRepository.save(contract);

        // 4. Audit Log
        auditService.logAction(
                contract.getId(),
                contract.getTitle(),
                currentUser,
                AuditAction.UPLOAD_CONTRACT,
                String.format("Created contract '%s' with %d clauses (Version 1.0)", contract.getTitle(), clauses.size()),
                1,
                ipAddress
        );

        // 5. Notify Reviewers and Admins
        for (User reviewer : userService.getReviewersAndAdmins()) {
            if (!reviewer.getId().equals(currentUser.getId())) {
                notificationService.notifyUser(
                        reviewer.getId(),
                        "New Contract Uploaded",
                        String.format("'%s' was uploaded by %s", contract.getTitle(), currentUser.getName()),
                        NotificationType.CONTRACT_UPLOADED,
                        contract.getId(),
                        contract.getTitle(),
                        1
                );
            }
        }

        return toResponse(contract, true);
    }

    public ContractResponse uploadDocument(MultipartFile file, String title, String contractType, List<String> parties, String description, String ipAddress) throws Exception {
        String fullText = documentParserService.extractTextFromFile(file);
        List<ClauseDTO> parsedClauses = documentParserService.segmentIntoClauses(fullText);

        CreateContractRequest request = CreateContractRequest.builder()
                .title(title != null && !title.isBlank() ? title : file.getOriginalFilename())
                .contractType(contractType != null && !contractType.isBlank() ? contractType : "Commercial Contract")
                .parties(parties != null && !parties.isEmpty() ? parties : List.of("Party A", "Party B"))
                .description(description)
                .fullText(fullText)
                .clauses(parsedClauses)
                .build();

        return createContract(request, ipAddress);
    }

    public ContractResponse updateContractMetadata(String id, UpdateContractRequest request, String ipAddress) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));

        User currentUser = authService.getCurrentUser();

        if (request.getTitle() != null) contract.setTitle(request.getTitle());
        if (request.getContractType() != null) contract.setContractType(request.getContractType());
        if (request.getParties() != null) contract.setParties(request.getParties());
        if (request.getDescription() != null) contract.setDescription(request.getDescription());
        if (request.getTags() != null) contract.setTags(request.getTags());
        if (request.getEffectiveDate() != null) contract.setEffectiveDate(request.getEffectiveDate());
        if (request.getExpirationDate() != null) contract.setExpirationDate(request.getExpirationDate());
        if (request.getContractValue() != null) contract.setContractValue(request.getContractValue());

        contract.setLastModifiedBy(currentUser.toReference());
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);

        auditService.logAction(
                saved.getId(),
                saved.getTitle(),
                currentUser,
                AuditAction.UPDATE_METADATA,
                "Updated contract metadata",
                saved.getCurrentVersionNumber(),
                ipAddress
        );

        return toResponse(saved, false);
    }

    public void archiveContract(String id, String ipAddress) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + id));

        User currentUser = authService.getCurrentUser();
        contract.setStatus(ContractStatus.ARCHIVED);
        contract.setUpdatedAt(Instant.now());
        contractRepository.save(contract);

        auditService.logAction(
                contract.getId(),
                contract.getTitle(),
                currentUser,
                AuditAction.ARCHIVE_CONTRACT,
                "Archived contract",
                contract.getCurrentVersionNumber(),
                ipAddress
        );
    }

    public ContractResponse toResponse(Contract contract, boolean includeFullVersions) {
        ContractResponse.Builder builder = ContractResponse.builder()
                .id(contract.getId())
                .title(contract.getTitle())
                .contractType(contract.getContractType())
                .parties(contract.getParties())
                .description(contract.getDescription())
                .tags(contract.getTags())
                .currentVersionNumber(contract.getCurrentVersionNumber())
                .currentVersionId(contract.getCurrentVersionId())
                .pendingVersionNumber(contract.getPendingVersionNumber())
                .pendingVersionId(contract.getPendingVersionId())
                .status(contract.getStatus())
                .createdBy(contract.getCreatedBy())
                .lastModifiedBy(contract.getLastModifiedBy())
                .effectiveDate(contract.getEffectiveDate())
                .expirationDate(contract.getExpirationDate())
                .contractValue(contract.getContractValue())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt());

        if (includeFullVersions) {
            if (contract.getCurrentVersionId() != null) {
                contractVersionRepository.findById(contract.getCurrentVersionId())
                        .ifPresent(v -> builder.activeVersion(toVersionResponse(v)));
            }
            if (contract.getPendingVersionId() != null) {
                contractVersionRepository.findById(contract.getPendingVersionId())
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
