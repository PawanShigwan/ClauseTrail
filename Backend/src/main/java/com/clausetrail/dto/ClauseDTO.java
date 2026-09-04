package com.clausetrail.dto;

public class ClauseDTO {
    private String id;
    private int orderIndex;
    private String clauseNumber;
    private String title;
    private String content;
    private String category;

    public ClauseDTO() {}

    public ClauseDTO(String id, int orderIndex, String clauseNumber, String title, String content, String category) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.clauseNumber = clauseNumber;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private int orderIndex;
        private String clauseNumber;
        private String title;
        private String content;
        private String category;

        public Builder id(String id) { this.id = id; return this; }
        public Builder orderIndex(int orderIndex) { this.orderIndex = orderIndex; return this; }
        public Builder clauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder category(String category) { this.category = category; return this; }

        public ClauseDTO build() {
            return new ClauseDTO(id, orderIndex, clauseNumber, title, content, category);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getClauseNumber() { return clauseNumber; }
    public void setClauseNumber(String clauseNumber) { this.clauseNumber = clauseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
