package com.clausetrail.dto;

import com.clausetrail.model.ContractStatus;
import com.clausetrail.model.UserReference;

import java.time.Instant;
import java.util.List;

public class ContractResponse {
    private String id;
    private String title;
    private String contractType;
    private List<String> parties;
    private String description;
    private List<String> tags;
    private int currentVersionNumber;
    private String currentVersionId;
    private Integer pendingVersionNumber;
    private String pendingVersionId;
    private ContractStatus status;
    private UserReference createdBy;
    private UserReference lastModifiedBy;
    private String effectiveDate;
    private String expirationDate;
    private String contractValue;
    private Instant createdAt;
    private Instant updatedAt;
    private VersionResponse activeVersion;
    private VersionResponse pendingVersion;

    public ContractResponse() {}

    public ContractResponse(String id, String title, String contractType, List<String> parties, String description, List<String> tags, int currentVersionNumber, String currentVersionId, Integer pendingVersionNumber, String pendingVersionId, ContractStatus status, UserReference createdBy, UserReference lastModifiedBy, String effectiveDate, String expirationDate, String contractValue, Instant createdAt, Instant updatedAt, VersionResponse activeVersion, VersionResponse pendingVersion) {
        this.id = id;
        this.title = title;
        this.contractType = contractType;
        this.parties = parties;
        this.description = description;
        this.tags = tags;
        this.currentVersionNumber = currentVersionNumber;
        this.currentVersionId = currentVersionId;
        this.pendingVersionNumber = pendingVersionNumber;
        this.pendingVersionId = pendingVersionId;
        this.status = status;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
        this.contractValue = contractValue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.activeVersion = activeVersion;
        this.pendingVersion = pendingVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private String contractType;
        private List<String> parties;
        private String description;
        private List<String> tags;
        private int currentVersionNumber;
        private String currentVersionId;
        private Integer pendingVersionNumber;
        private String pendingVersionId;
        private ContractStatus status;
        private UserReference createdBy;
        private UserReference lastModifiedBy;
        private String effectiveDate;
        private String expirationDate;
        private String contractValue;
        private Instant createdAt;
        private Instant updatedAt;
        private VersionResponse activeVersion;
        private VersionResponse pendingVersion;

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder contractType(String contractType) { this.contractType = contractType; return this; }
        public Builder parties(List<String> parties) { this.parties = parties; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder tags(List<String> tags) { this.tags = tags; return this; }
        public Builder currentVersionNumber(int currentVersionNumber) { this.currentVersionNumber = currentVersionNumber; return this; }
        public Builder currentVersionId(String currentVersionId) { this.currentVersionId = currentVersionId; return this; }
        public Builder pendingVersionNumber(Integer pendingVersionNumber) { this.pendingVersionNumber = pendingVersionNumber; return this; }
        public Builder pendingVersionId(String pendingVersionId) { this.pendingVersionId = pendingVersionId; return this; }
        public Builder status(ContractStatus status) { this.status = status; return this; }
        public Builder createdBy(UserReference createdBy) { this.createdBy = createdBy; return this; }
        public Builder lastModifiedBy(UserReference lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; return this; }
        public Builder effectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; return this; }
        public Builder expirationDate(String expirationDate) { this.expirationDate = expirationDate; return this; }
        public Builder contractValue(String contractValue) { this.contractValue = contractValue; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder activeVersion(VersionResponse activeVersion) { this.activeVersion = activeVersion; return this; }
        public Builder pendingVersion(VersionResponse pendingVersion) { this.pendingVersion = pendingVersion; return this; }

        public ContractResponse build() {
            return new ContractResponse(id, title, contractType, parties, description, tags, currentVersionNumber, currentVersionId, pendingVersionNumber, pendingVersionId, status, createdBy, lastModifiedBy, effectiveDate, expirationDate, contractValue, createdAt, updatedAt, activeVersion, pendingVersion);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public List<String> getParties() { return parties; }
    public void setParties(List<String> parties) { this.parties = parties; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getCurrentVersionNumber() { return currentVersionNumber; }
    public void setCurrentVersionNumber(int currentVersionNumber) { this.currentVersionNumber = currentVersionNumber; }

    public String getCurrentVersionId() { return currentVersionId; }
    public void setCurrentVersionId(String currentVersionId) { this.currentVersionId = currentVersionId; }

    public Integer getPendingVersionNumber() { return pendingVersionNumber; }
    public void setPendingVersionNumber(Integer pendingVersionNumber) { this.pendingVersionNumber = pendingVersionNumber; }

    public String getPendingVersionId() { return pendingVersionId; }
    public void setPendingVersionId(String pendingVersionId) { this.pendingVersionId = pendingVersionId; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }

    public UserReference getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserReference createdBy) { this.createdBy = createdBy; }

    public UserReference getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(UserReference lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public String getContractValue() { return contractValue; }
    public void setContractValue(String contractValue) { this.contractValue = contractValue; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public VersionResponse getActiveVersion() { return activeVersion; }
    public void setActiveVersion(VersionResponse activeVersion) { this.activeVersion = activeVersion; }

    public VersionResponse getPendingVersion() { return pendingVersion; }
    public void setPendingVersion(VersionResponse pendingVersion) { this.pendingVersion = pendingVersion; }
}
