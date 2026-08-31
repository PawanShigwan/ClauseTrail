package com.clausetrail.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class ModifyContractRequest {
    @NotBlank(message = "Modification reason is mandatory")
    private String modificationReason;
    private String fullText;
    private List<ClauseDTO> clauses = new ArrayList<>();
    private List<ClauseChangeDTO> clauseChanges = new ArrayList<>();

    public ModifyContractRequest() {}

    public ModifyContractRequest(String modificationReason, String fullText, List<ClauseDTO> clauses, List<ClauseChangeDTO> clauseChanges) {
        this.modificationReason = modificationReason;
        this.fullText = fullText;
        this.clauses = clauses != null ? clauses : new ArrayList<>();
        this.clauseChanges = clauseChanges != null ? clauseChanges : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String modificationReason;
        private String fullText;
        private List<ClauseDTO> clauses = new ArrayList<>();
        private List<ClauseChangeDTO> clauseChanges = new ArrayList<>();

        public Builder modificationReason(String modificationReason) { this.modificationReason = modificationReason; return this; }
        public Builder fullText(String fullText) { this.fullText = fullText; return this; }
        public Builder clauses(List<ClauseDTO> clauses) { this.clauses = clauses; return this; }
        public Builder clauseChanges(List<ClauseChangeDTO> clauseChanges) { this.clauseChanges = clauseChanges; return this; }

        public ModifyContractRequest build() {
            return new ModifyContractRequest(modificationReason, fullText, clauses, clauseChanges);
        }
    }

    public String getModificationReason() { return modificationReason; }
    public void setModificationReason(String modificationReason) { this.modificationReason = modificationReason; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public List<ClauseDTO> getClauses() { return clauses; }
    public void setClauses(List<ClauseDTO> clauses) { this.clauses = clauses; }

    public List<ClauseChangeDTO> getClauseChanges() { return clauseChanges; }
    public void setClauseChanges(List<ClauseChangeDTO> clauseChanges) { this.clauseChanges = clauseChanges; }
}
