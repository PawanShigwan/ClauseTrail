package com.clausetrail.controller;

import com.clausetrail.dto.ContractResponse;
import com.clausetrail.dto.CreateContractRequest;
import com.clausetrail.dto.UpdateContractRequest;
import com.clausetrail.service.ContractService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public ResponseEntity<List<ContractResponse>> getAllContracts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType
    ) {
        return ResponseEntity.ok(contractService.getAllContracts(search, status, contractType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(
            @PathVariable String id,
            @RequestParam(defaultValue = "true") boolean full
    ) {
        return ResponseEntity.ok(contractService.getContractById(id, full));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody CreateContractRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(contractService.createContract(request, ip));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ContractResponse> uploadContractDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "contractType", required = false) String contractType,
            @RequestParam(value = "parties", required = false) List<String> parties,
            @RequestParam(value = "description", required = false) String description,
            HttpServletRequest httpRequest
    ) throws Exception {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(contractService.uploadDocument(file, title, contractType, parties, description, ip));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ContractResponse> updateContractMetadata(
            @PathVariable String id,
            @RequestBody UpdateContractRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(contractService.updateContractMetadata(id, request, ip));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<Void> archiveContract(
            @PathVariable String id,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        contractService.archiveContract(id, ip);
        return ResponseEntity.noContent().build();
    }
}
