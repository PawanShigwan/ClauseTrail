package com.clausetrail.dto;

import com.clausetrail.model.VersionStatus;
import jakarta.validation.constraints.NotNull;

public class ReviewRequest {
    @NotNull(message = "Action is required (APPROVED or REJECTED)")
    private VersionStatus action;
    private String reviewComments;

    public ReviewRequest() {}

    public ReviewRequest(VersionStatus action, String reviewComments) {
        this.action = action;
        this.reviewComments = reviewComments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VersionStatus action;
        private String reviewComments;

        public Builder action(VersionStatus action) { this.action = action; return this; }
        public Builder reviewComments(String reviewComments) { this.reviewComments = reviewComments; return this; }

        public ReviewRequest build() {
            return new ReviewRequest(action, reviewComments);
        }
    }

    public VersionStatus getAction() { return action; }
    public void setAction(VersionStatus action) { this.action = action; }

    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }
}
