package com.clausetrail.controller;

import com.clausetrail.dto.AuditLogResponse;
import com.clausetrail.service.AuditService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String contractId,
            @RequestParam(required = false) String action
    ) {
        return ResponseEntity.ok(auditService.getAllAuditLogs(contractId, action));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public ResponseEntity<byte[]> exportAuditLogsToCsv(
            @RequestParam(required = false) String contractId
    ) {
        byte[] csv = auditService.exportAuditLogsToCsv(contractId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "clausetrail-audit-log.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
