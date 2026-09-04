package com.clausetrail.dto;

import com.clausetrail.model.AuditAction;
import com.clausetrail.model.Role;
import java.time.Instant;

public class AuditLogResponse {
    private String id;
    private String contractId;
    private String contractTitle;
    private String userId;
    private String userName;
    private Role userRole;
    private AuditAction action;
    private String details;
    private Integer versionNumber;
    private String ipAddress;
    private Instant timestamp;

    public AuditLogResponse() {}

    public AuditLogResponse(String id, String contractId, String contractTitle, String userId, String userName, Role userRole, AuditAction action, String details, Integer versionNumber, String ipAddress, Instant timestamp) {
        this.id = id;
        this.contractId = contractId;
        this.contractTitle = contractTitle;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.action = action;
        this.details = details;
        this.versionNumber = versionNumber;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String contractId;
        private String contractTitle;
        private String userId;
        private String userName;
        private Role userRole;
        private AuditAction action;
        private String details;
        private Integer versionNumber;
        private String ipAddress;
        private Instant timestamp;

        public Builder id(String id) { this.id = id; return this; }
        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder contractTitle(String contractTitle) { this.contractTitle = contractTitle; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder userRole(Role userRole) { this.userRole = userRole; return this; }
        public Builder action(AuditAction action) { this.action = action; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder versionNumber(Integer versionNumber) { this.versionNumber = versionNumber; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public AuditLogResponse build() {
            return new AuditLogResponse(id, contractId, contractTitle, userId, userName, userRole, action, details, versionNumber, ipAddress, timestamp);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Role getUserRole() { return userRole; }
    public void setUserRole(Role userRole) { this.userRole = userRole; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
