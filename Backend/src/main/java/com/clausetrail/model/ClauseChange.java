package com.clausetrail.model;

public class ClauseChange {
    private String clauseId;
    private String clauseNumber;
    private String clauseTitle;
    private String previousText;
    private String modifiedText;
    private ChangeType changeType;

    public ClauseChange() {}

    public ClauseChange(String clauseId, String clauseNumber, String clauseTitle, String previousText, String modifiedText, ChangeType changeType) {
        this.clauseId = clauseId;
        this.clauseNumber = clauseNumber;
        this.clauseTitle = clauseTitle;
        this.previousText = previousText;
        this.modifiedText = modifiedText;
        this.changeType = changeType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String clauseId;
        private String clauseNumber;
        private String clauseTitle;
        private String previousText;
        private String modifiedText;
        private ChangeType changeType;

        public Builder clauseId(String clauseId) { this.clauseId = clauseId; return this; }
        public Builder clauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; return this; }
        public Builder clauseTitle(String clauseTitle) { this.clauseTitle = clauseTitle; return this; }
        public Builder previousText(String previousText) { this.previousText = previousText; return this; }
        public Builder modifiedText(String modifiedText) { this.modifiedText = modifiedText; return this; }
        public Builder changeType(ChangeType changeType) { this.changeType = changeType; return this; }

        public ClauseChange build() {
            return new ClauseChange(clauseId, clauseNumber, clauseTitle, previousText, modifiedText, changeType);
        }
    }

    public String getClauseId() { return clauseId; }
    public void setClauseId(String clauseId) { this.clauseId = clauseId; }

    public String getClauseNumber() { return clauseNumber; }
    public void setClauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; }

    public String getClauseTitle() { return clauseTitle; }
    public void setClauseTitle(String clauseTitle) { this.clauseTitle = clauseTitle; }

    public String getPreviousText() { return previousText; }
    public void setPreviousText(String previousText) { this.previousText = previousText; }

    public String getModifiedText() { return modifiedText; }
    public void setModifiedText(String modifiedText) { this.modifiedText = modifiedText; }

    public ChangeType getChangeType() { return changeType; }
    public void setChangeType(ChangeType changeType) { this.changeType = changeType; }
}
