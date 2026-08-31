package com.clausetrail.dto;

import java.util.List;

public class UpdateContractRequest {
    private String title;
    private String contractType;
    private List<String> parties;
    private String description;
    private List<String> tags;
    private String effectiveDate;
    private String expirationDate;
    private String contractValue;

    public UpdateContractRequest() {}

    public UpdateContractRequest(String title, String contractType, List<String> parties, String description, List<String> tags, String effectiveDate, String expirationDate, String contractValue) {
        this.title = title;
        this.contractType = contractType;
        this.parties = parties;
        this.description = description;
        this.tags = tags;
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
        this.contractValue = contractValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String contractType;
        private List<String> parties;
        private String description;
        private List<String> tags;
        private String effectiveDate;
        private String expirationDate;
        private String contractValue;

        public Builder title(String title) { this.title = title; return this; }
        public Builder contractType(String contractType) { this.contractType = contractType; return this; }
        public Builder parties(List<String> parties) { this.parties = parties; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder tags(List<String> tags) { this.tags = tags; return this; }
        public Builder effectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; return this; }
        public Builder expirationDate(String expirationDate) { this.expirationDate = expirationDate; return this; }
        public Builder contractValue(String contractValue) { this.contractValue = contractValue; return this; }

        public UpdateContractRequest build() {
            return new UpdateContractRequest(title, contractType, parties, description, tags, effectiveDate, expirationDate, contractValue);
        }
    }

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

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public String getContractValue() { return contractValue; }
    public void setContractValue(String contractValue) { this.contractValue = contractValue; }
}
