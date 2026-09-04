package com.clausetrail.controller;

import com.clausetrail.dto.ContractResponse;
import com.clausetrail.dto.ReviewQueueItemDTO;
import com.clausetrail.dto.ReviewRequest;
import com.clausetrail.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/review-queue")
    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    public ResponseEntity<List<ReviewQueueItemDTO>> getReviewQueue() {
        return ResponseEntity.ok(reviewService.getReviewQueue());
    }

    @PostMapping("/api/contracts/{id}/review")
    @PreAuthorize("hasAnyRole('REVIEWER', 'ADMIN')")
    public ResponseEntity<ContractResponse> reviewModification(
            @PathVariable String id,
            @Valid @RequestBody ReviewRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(reviewService.reviewModification(id, request, ip));
    }
}
