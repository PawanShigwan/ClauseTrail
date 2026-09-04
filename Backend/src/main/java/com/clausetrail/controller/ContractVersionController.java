package com.clausetrail.controller;

import com.clausetrail.dto.ContractResponse;
import com.clausetrail.dto.DiffResponse;
import com.clausetrail.dto.ModifyContractRequest;
import com.clausetrail.dto.VersionResponse;
import com.clausetrail.service.ContractVersionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/{id}")
public class ContractVersionController {

    private final ContractVersionService contractVersionService;

    public ContractVersionController(ContractVersionService contractVersionService) {
        this.contractVersionService = contractVersionService;
    }

    @PostMapping("/modify")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ContractResponse> submitModification(
            @PathVariable String id,
            @Valid @RequestBody ModifyContractRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(contractVersionService.submitModification(id, request, ip));
    }

    @GetMapping("/versions")
    public ResponseEntity<List<VersionResponse>> getVersionHistory(@PathVariable String id) {
        return ResponseEntity.ok(contractVersionService.getVersionHistory(id));
    }

    @GetMapping("/versions/{versionNumber}")
    public ResponseEntity<VersionResponse> getVersionByNumber(
            @PathVariable String id,
            @PathVariable int versionNumber
    ) {
        return ResponseEntity.ok(contractVersionService.getVersionByNumber(id, versionNumber));
    }

    @GetMapping("/diff/{v1}/{v2}")
    public ResponseEntity<DiffResponse> getDiffBetweenVersions(
            @PathVariable String id,
            @PathVariable int v1,
            @PathVariable int v2
    ) {
        return ResponseEntity.ok(contractVersionService.getDiffBetweenVersions(id, v1, v2));
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportVersionPdf(
            @PathVariable String id,
            @RequestParam(required = false) Integer version
    ) throws Exception {
        byte[] pdf = contractVersionService.exportVersionPdf(id, version);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contract-" + id + "-v" + (version != null ? version : "current") + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
