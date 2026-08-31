package com.clausetrail.dto;

import com.clausetrail.model.ClauseChange;
import java.util.List;

public class DiffResponse {
    private String contractId;
    private String contractTitle;
    private int v1Number;
    private int v2Number;
    private VersionResponse v1;
    private VersionResponse v2;
    private List<ClauseDiffDTO> clauseDiffs;
    private List<DiffSegmentDTO> fullTextDiffSegments;
    private int totalClausesChanged;
    private int additionsCount;
    private int deletionsCount;

    public DiffResponse() {}

    public DiffResponse(String contractId, String contractTitle, int v1Number, int v2Number, VersionResponse v1, VersionResponse v2, List<ClauseDiffDTO> clauseDiffs, List<DiffSegmentDTO> fullTextDiffSegments, int totalClausesChanged, int additionsCount, int deletionsCount) {
        this.contractId = contractId;
        this.contractTitle = contractTitle;
        this.v1Number = v1Number;
        this.v2Number = v2Number;
        this.v1 = v1;
        this.v2 = v2;
        this.clauseDiffs = clauseDiffs;
        this.fullTextDiffSegments = fullTextDiffSegments;
        this.totalClausesChanged = totalClausesChanged;
        this.additionsCount = additionsCount;
        this.deletionsCount = deletionsCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String contractId;
        private String contractTitle;
        private int v1Number;
        private int v2Number;
        private VersionResponse v1;
        private VersionResponse v2;
        private List<ClauseDiffDTO> clauseDiffs;
        private List<DiffSegmentDTO> fullTextDiffSegments;
        private int totalClausesChanged;
        private int additionsCount;
        private int deletionsCount;

        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder contractTitle(String contractTitle) { this.contractTitle = contractTitle; return this; }
        public Builder v1Number(int v1Number) { this.v1Number = v1Number; return this; }
        public Builder v2Number(int v2Number) { this.v2Number = v2Number; return this; }
        public Builder v1(VersionResponse v1) { this.v1 = v1; return this; }
        public Builder v2(VersionResponse v2) { this.v2 = v2; return this; }
        public Builder clauseDiffs(List<ClauseDiffDTO> clauseDiffs) { this.clauseDiffs = clauseDiffs; return this; }
        public Builder fullTextDiffSegments(List<DiffSegmentDTO> fullTextDiffSegments) { this.fullTextDiffSegments = fullTextDiffSegments; return this; }
        public Builder totalClausesChanged(int totalClausesChanged) { this.totalClausesChanged = totalClausesChanged; return this; }
        public Builder additionsCount(int additionsCount) { this.additionsCount = additionsCount; return this; }
        public Builder deletionsCount(int deletionsCount) { this.deletionsCount = deletionsCount; return this; }

        public DiffResponse build() {
            return new DiffResponse(contractId, contractTitle, v1Number, v2Number, v1, v2, clauseDiffs, fullTextDiffSegments, totalClausesChanged, additionsCount, deletionsCount);
        }
    }

    public static class ClauseDiffDTO {
        private String clauseId;
        private String clauseNumber;
        private String clauseTitle;
        private String v1Text;
        private String v2Text;
        private List<DiffSegmentDTO> segments;
        private ClauseChange changeSummary;

        public ClauseDiffDTO() {}

        public ClauseDiffDTO(String clauseId, String clauseNumber, String clauseTitle, String v1Text, String v2Text, List<DiffSegmentDTO> segments, ClauseChange changeSummary) {
            this.clauseId = clauseId;
            this.clauseNumber = clauseNumber;
            this.clauseTitle = clauseTitle;
            this.v1Text = v1Text;
            this.v2Text = v2Text;
            this.segments = segments;
            this.changeSummary = changeSummary;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String clauseId;
            private String clauseNumber;
            private String clauseTitle;
            private String v1Text;
            private String v2Text;
            private List<DiffSegmentDTO> segments;
            private ClauseChange changeSummary;

            public Builder clauseId(String clauseId) { this.clauseId = clauseId; return this; }
            public Builder clauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; return this; }
            public Builder clauseTitle(String clauseTitle) { this.clauseTitle = clauseTitle; return this; }
            public Builder v1Text(String v1Text) { this.v1Text = v1Text; return this; }
            public Builder v2Text(String v2Text) { this.v2Text = v2Text; return this; }
            public Builder segments(List<DiffSegmentDTO> segments) { this.segments = segments; return this; }
            public Builder changeSummary(ClauseChange changeSummary) { this.changeSummary = changeSummary; return this; }

            public ClauseDiffDTO build() {
                return new ClauseDiffDTO(clauseId, clauseNumber, clauseTitle, v1Text, v2Text, segments, changeSummary);
            }
        }

        public String getClauseId() { return clauseId; }
        public void setClauseId(String clauseId) { this.clauseId = clauseId; }

        public String getClauseNumber() { return clauseNumber; }
        public void setClauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; }

        public String getClauseTitle() { return clauseTitle; }
        public void setClauseTitle(String clauseTitle) { this.clauseTitle = clauseTitle; }

        public String getV1Text() { return v1Text; }
        public void setV1Text(String v1Text) { this.v1Text = v1Text; }

        public String getV2Text() { return v2Text; }
        public void setV2Text(String v2Text) { this.v2Text = v2Text; }

        public List<DiffSegmentDTO> getSegments() { return segments; }
        public void setSegments(List<DiffSegmentDTO> segments) { this.segments = segments; }

        public ClauseChange getChangeSummary() { return changeSummary; }
        public void setChangeSummary(ClauseChange changeSummary) { this.changeSummary = changeSummary; }
    }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }

    public int getV1Number() { return v1Number; }
    public void setV1Number(int v1Number) { this.v1Number = v1Number; }

    public int getV2Number() { return v2Number; }
    public void setV2Number(int v2Number) { this.v2Number = v2Number; }

    public VersionResponse getV1() { return v1; }
    public void setV1(VersionResponse v1) { this.v1 = v1; }

    public VersionResponse getV2() { return v2; }
    public void setV2(VersionResponse v2) { this.v2 = v2; }

    public List<ClauseDiffDTO> getClauseDiffs() { return clauseDiffs; }
    public void setClauseDiffs(List<ClauseDiffDTO> clauseDiffs) { this.clauseDiffs = clauseDiffs; }

    public List<DiffSegmentDTO> getFullTextDiffSegments() { return fullTextDiffSegments; }
    public void setFullTextDiffSegments(List<DiffSegmentDTO> fullTextDiffSegments) { this.fullTextDiffSegments = fullTextDiffSegments; }

    public int getTotalClausesChanged() { return totalClausesChanged; }
    public void setTotalClausesChanged(int totalClausesChanged) { this.totalClausesChanged = totalClausesChanged; }

    public int getAdditionsCount() { return additionsCount; }
    public void setAdditionsCount(int additionsCount) { this.additionsCount = additionsCount; }

    public int getDeletionsCount() { return deletionsCount; }
    public void setDeletionsCount(int deletionsCount) { this.deletionsCount = deletionsCount; }
}
