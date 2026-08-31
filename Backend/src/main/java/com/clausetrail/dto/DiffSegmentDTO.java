package com.clausetrail.dto;

import com.clausetrail.model.ChangeType;

public class DiffSegmentDTO {
    private ChangeType type;
    private String text;

    public DiffSegmentDTO() {}

    public DiffSegmentDTO(ChangeType type, String text) {
        this.type = type;
        this.text = text;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChangeType type;
        private String text;

        public Builder type(ChangeType type) { this.type = type; return this; }
        public Builder text(String text) { this.text = text; return this; }

        public DiffSegmentDTO build() {
            return new DiffSegmentDTO(type, text);
        }
    }

    public ChangeType getType() { return type; }
    public void setType(ChangeType type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
