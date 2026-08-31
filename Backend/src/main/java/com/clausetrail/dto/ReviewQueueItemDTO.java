package com.clausetrail.dto;

import com.clausetrail.model.ClauseChange;
import com.clausetrail.model.UserReference;

import java.time.Instant;
import java.util.List;

public class ReviewQueueItemDTO {
    private String contractId;
    private String contractTitle;
    private String contractType;
    private List<String> parties;
    private String versionId;
    private int versionNumber;
    private int parentVersionNumber;
    private String modificationReason;
    private UserReference modifiedBy;
    private Instant submittedAt;
    private int clausesChangedCount;
    private List<ClauseChange> changesPreview;

    public ReviewQueueItemDTO() {}

    public ReviewQueueItemDTO(String contractId, String contractTitle, String contractType, List<String> parties, String versionId, int versionNumber, int parentVersionNumber, String modificationReason, UserReference modifiedBy, Instant submittedAt, int clausesChangedCount, List<ClauseChange> changesPreview) {
        this.contractId = contractId;
        this.contractTitle = contractTitle;
        this.contractType = contractType;
        this.parties = parties;
        this.versionId = versionId;
        this.versionNumber = versionNumber;
        this.parentVersionNumber = parentVersionNumber;
        this.modificationReason = modificationReason;
        this.modifiedBy = modifiedBy;
        this.submittedAt = submittedAt;
        this.clausesChangedCount = clausesChangedCount;
        this.changesPreview = changesPreview;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String contractId;
        private String contractTitle;
        private String contractType;
        private List<String> parties;
        private String versionId;
        private int versionNumber;
        private int parentVersionNumber;
        private String modificationReason;
        private UserReference modifiedBy;
        private Instant submittedAt;
        private int clausesChangedCount;
        private List<ClauseChange> changesPreview;

        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder contractTitle(String contractTitle) { this.contractTitle = contractTitle; return this; }
        public Builder contractType(String contractType) { this.contractType = contractType; return this; }
        public Builder parties(List<String> parties) { this.parties = parties; return this; }
        public Builder versionId(String versionId) { this.versionId = versionId; return this; }
        public Builder versionNumber(int versionNumber) { this.versionNumber = versionNumber; return this; }
        public Builder parentVersionNumber(int parentVersionNumber) { this.parentVersionNumber = parentVersionNumber; return this; }
        public Builder modificationReason(String modificationReason) { this.modificationReason = modificationReason; return this; }
        public Builder modifiedBy(UserReference modifiedBy) { this.modifiedBy = modifiedBy; return this; }
        public Builder submittedAt(Instant submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder clausesChangedCount(int clausesChangedCount) { this.clausesChangedCount = clausesChangedCount; return this; }
        public Builder changesPreview(List<ClauseChange> changesPreview) { this.changesPreview = changesPreview; return this; }

        public ReviewQueueItemDTO build() {
            return new ReviewQueueItemDTO(contractId, contractTitle, contractType, parties, versionId, versionNumber, parentVersionNumber, modificationReason, modifiedBy, submittedAt, clausesChangedCount, changesPreview);
        }
    }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public List<String> getParties() { return parties; }
    public void setParties(List<String> parties) { this.parties = parties; }

    public String getVersionId() { return versionId; }
    public void setVersionId(String versionId) { this.versionId = versionId; }

    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }

    public int getParentVersionNumber() { return parentVersionNumber; }
    public void setParentVersionNumber(int parentVersionNumber) { this.parentVersionNumber = parentVersionNumber; }

    public String getModificationReason() { return modificationReason; }
    public void setModificationReason(String modificationReason) { this.modificationReason = modificationReason; }

    public UserReference getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(UserReference modifiedBy) { this.modifiedBy = modifiedBy; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    public int getClausesChangedCount() { return clausesChangedCount; }
    public void setClausesChangedCount(int clausesChangedCount) { this.clausesChangedCount = clausesChangedCount; }

    public List<ClauseChange> getChangesPreview() { return changesPreview; }
    public void setChangesPreview(List<ClauseChange> changesPreview) { this.changesPreview = changesPreview; }
}
