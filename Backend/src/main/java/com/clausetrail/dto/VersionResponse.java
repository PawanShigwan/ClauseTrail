package com.clausetrail.dto;

import com.clausetrail.model.Clause;
import com.clausetrail.model.ClauseChange;
import com.clausetrail.model.UserReference;
import com.clausetrail.model.VersionStatus;

import java.time.Instant;
import java.util.List;

public class VersionResponse {
    private String id;
    private String contractId;
    private int versionNumber;
    private String fullText;
    private List<Clause> clauses;
    private List<ClauseChange> clauseChanges;
    private UserReference modifiedBy;
    private String modificationReason;
    private VersionStatus status;
    private UserReference reviewedBy;
    private String reviewComments;
    private Instant reviewedAt;
    private String parentVersionId;
    private Integer parentVersionNumber;
    private Instant createdAt;

    public VersionResponse() {}

    public VersionResponse(String id, String contractId, int versionNumber, String fullText, List<Clause> clauses, List<ClauseChange> clauseChanges, UserReference modifiedBy, String modificationReason, VersionStatus status, UserReference reviewedBy, String reviewComments, Instant reviewedAt, String parentVersionId, Integer parentVersionNumber, Instant createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.versionNumber = versionNumber;
        this.fullText = fullText;
        this.clauses = clauses;
        this.clauseChanges = clauseChanges;
        this.modifiedBy = modifiedBy;
        this.modificationReason = modificationReason;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.reviewComments = reviewComments;
        this.reviewedAt = reviewedAt;
        this.parentVersionId = parentVersionId;
        this.parentVersionNumber = parentVersionNumber;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String contractId;
        private int versionNumber;
        private String fullText;
        private List<Clause> clauses;
        private List<ClauseChange> clauseChanges;
        private UserReference modifiedBy;
        private String modificationReason;
        private VersionStatus status;
        private UserReference reviewedBy;
        private String reviewComments;
        private Instant reviewedAt;
        private String parentVersionId;
        private Integer parentVersionNumber;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder versionNumber(int versionNumber) { this.versionNumber = versionNumber; return this; }
        public Builder fullText(String fullText) { this.fullText = fullText; return this; }
        public Builder clauses(List<Clause> clauses) { this.clauses = clauses; return this; }
        public Builder clauseChanges(List<ClauseChange> clauseChanges) { this.clauseChanges = clauseChanges; return this; }
        public Builder modifiedBy(UserReference modifiedBy) { this.modifiedBy = modifiedBy; return this; }
        public Builder modificationReason(String modificationReason) { this.modificationReason = modificationReason; return this; }
        public Builder status(VersionStatus status) { this.status = status; return this; }
        public Builder reviewedBy(UserReference reviewedBy) { this.reviewedBy = reviewedBy; return this; }
        public Builder reviewComments(String reviewComments) { this.reviewComments = reviewComments; return this; }
        public Builder reviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; return this; }
        public Builder parentVersionId(String parentVersionId) { this.parentVersionId = parentVersionId; return this; }
        public Builder parentVersionNumber(Integer parentVersionNumber) { this.parentVersionNumber = parentVersionNumber; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public VersionResponse build() {
            return new VersionResponse(id, contractId, versionNumber, fullText, clauses, clauseChanges, modifiedBy, modificationReason, status, reviewedBy, reviewComments, reviewedAt, parentVersionId, parentVersionNumber, createdAt);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public List<Clause> getClauses() { return clauses; }
    public void setClauses(List<Clause> clauses) { this.clauses = clauses; }

    public List<ClauseChange> getClauseChanges() { return clauseChanges; }
    public void setClauseChanges(List<ClauseChange> clauseChanges) { this.clauseChanges = clauseChanges; }

    public UserReference getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(UserReference modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getModificationReason() { return modificationReason; }
    public void setModificationReason(String modificationReason) { this.modificationReason = modificationReason; }

    public VersionStatus getStatus() { return status; }
    public void setStatus(VersionStatus status) { this.status = status; }

    public UserReference getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserReference reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }

    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getParentVersionId() { return parentVersionId; }
    public void setParentVersionId(String parentVersionId) { this.parentVersionId = parentVersionId; }

    public Integer getParentVersionNumber() { return parentVersionNumber; }
    public void setParentVersionNumber(Integer parentVersionNumber) { this.parentVersionNumber = parentVersionNumber; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
