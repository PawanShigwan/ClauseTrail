package com.clausetrail.service;

import com.clausetrail.dto.AuditLogResponse;
import com.clausetrail.model.AuditAction;
import com.clausetrail.model.AuditLog;
import com.clausetrail.model.User;
import com.clausetrail.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog logAction(
            String contractId,
            String contractTitle,
            User user,
            AuditAction action,
            String details,
            Integer versionNumber,
            String ipAddress
    ) {
        AuditLog auditLog = AuditLog.builder()
                .contractId(contractId)
                .contractTitle(contractTitle)
                .userId(user != null ? user.getId() : "system")
                .userName(user != null ? user.getName() : "System")
                .userRole(user != null ? user.getRole() : null)
                .action(action)
                .details(details)
                .versionNumber(versionNumber)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .timestamp(Instant.now())
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);
        log.info("Audit log recorded: [{}] user='{}', contract='{}', action='{}', details='{}'",
                saved.getId(), saved.getUserName(), saved.getContractTitle(), saved.getAction(), saved.getDetails());
        return saved;
    }

    public List<AuditLogResponse> getAllAuditLogs(String contractId, String actionStr) {
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        List<AuditLog> logs;

        if (contractId != null && !contractId.isBlank()) {
            logs = auditLogRepository.findByContractId(contractId, sort);
        } else if (actionStr != null && !actionStr.isBlank()) {
            try {
                AuditAction action = AuditAction.valueOf(actionStr.toUpperCase());
                logs = auditLogRepository.findByAction(action, sort);
            } catch (IllegalArgumentException e) {
                logs = auditLogRepository.findAll(sort);
            }
        } else {
            logs = auditLogRepository.findAll(sort);
        }

        return logs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public byte[] exportAuditLogsToCsv(String contractId) {
        List<AuditLogResponse> logs = getAllAuditLogs(contractId, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("Timestamp,Action,User,Role,Contract Title,Version,Details,IP Address");
            for (AuditLogResponse logEntry : logs) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        logEntry.getTimestamp() != null ? logEntry.getTimestamp().toString() : "",
                        logEntry.getAction() != null ? logEntry.getAction().name() : "",
                        escapeCsv(logEntry.getUserName()),
                        logEntry.getUserRole() != null ? logEntry.getUserRole().name() : "",
                        escapeCsv(logEntry.getContractTitle() != null ? logEntry.getContractTitle() : ""),
                        logEntry.getVersionNumber() != null ? "v" + logEntry.getVersionNumber() : "-",
                        escapeCsv(logEntry.getDetails()),
                        logEntry.getIpAddress() != null ? logEntry.getIpAddress() : ""
                );
            }
            writer.flush();
        }
        return out.toByteArray();
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }

    public AuditLogResponse toResponse(AuditLog l) {
        return AuditLogResponse.builder()
                .id(l.getId())
                .contractId(l.getContractId())
                .contractTitle(l.getContractTitle())
                .userId(l.getUserId())
                .userName(l.getUserName())
                .userRole(l.getUserRole())
                .action(l.getAction())
                .details(l.getDetails())
                .versionNumber(l.getVersionNumber())
                .ipAddress(l.getIpAddress())
                .timestamp(l.getTimestamp())
                .build();
    }
}
