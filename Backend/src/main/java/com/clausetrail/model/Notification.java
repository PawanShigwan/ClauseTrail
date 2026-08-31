package com.clausetrail.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private String contractId;
    private String contractTitle;
    private Integer versionNumber;
    private boolean read = false;

    @CreatedDate
    @Indexed
    private Instant createdAt;

    public Notification() {}

    public Notification(String id, String userId, String title, String message, NotificationType type, String contractId, String contractTitle, Integer versionNumber, boolean read, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.contractId = contractId;
        this.contractTitle = contractTitle;
        this.versionNumber = versionNumber;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String title;
        private String message;
        private NotificationType type;
        private String contractId;
        private String contractTitle;
        private Integer versionNumber;
        private boolean read = false;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder type(NotificationType type) { this.type = type; return this; }
        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder contractTitle(String contractTitle) { this.contractTitle = contractTitle; return this; }
        public Builder versionNumber(Integer versionNumber) { this.versionNumber = versionNumber; return this; }
        public Builder read(boolean read) { this.read = read; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public Notification build() {
            return new Notification(id, userId, title, message, type, contractId, contractTitle, versionNumber, read, createdAt);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
