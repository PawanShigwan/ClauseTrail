package com.clausetrail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class CreateContractRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Contract type is required")
    private String contractType;

    @NotEmpty(message = "At least one party must be specified")
    private List<String> parties = new ArrayList<>();

    private String description;
    private List<String> tags = new ArrayList<>();
    private String effectiveDate;
    private String expirationDate;
    private String contractValue;
    private String fullText;
    private List<ClauseDTO> clauses = new ArrayList<>();

    public CreateContractRequest() {}

    public CreateContractRequest(String title, String contractType, List<String> parties, String description, List<String> tags, String effectiveDate, String expirationDate, String contractValue, String fullText, List<ClauseDTO> clauses) {
        this.title = title;
        this.contractType = contractType;
        this.parties = parties != null ? parties : new ArrayList<>();
        this.description = description;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
        this.contractValue = contractValue;
        this.fullText = fullText;
        this.clauses = clauses != null ? clauses : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String contractType;
        private List<String> parties = new ArrayList<>();
        private String description;
        private List<String> tags = new ArrayList<>();
        private String effectiveDate;
        private String expirationDate;
        private String contractValue;
        private String fullText;
        private List<ClauseDTO> clauses = new ArrayList<>();

        public Builder title(String title) { this.title = title; return this; }
        public Builder contractType(String contractType) { this.contractType = contractType; return this; }
        public Builder parties(List<String> parties) { this.parties = parties; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder tags(List<String> tags) { this.tags = tags; return this; }
        public Builder effectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; return this; }
        public Builder expirationDate(String expirationDate) { this.expirationDate = expirationDate; return this; }
        public Builder contractValue(String contractValue) { this.contractValue = contractValue; return this; }
        public Builder fullText(String fullText) { this.fullText = fullText; return this; }
        public Builder clauses(List<ClauseDTO> clauses) { this.clauses = clauses; return this; }

        public CreateContractRequest build() {
            return new CreateContractRequest(title, contractType, parties, description, tags, effectiveDate, expirationDate, contractValue, fullText, clauses);
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

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public List<ClauseDTO> getClauses() { return clauses; }
    public void setClauses(List<ClauseDTO> clauses) { this.clauses = clauses; }
}
