package com.clausetrail.dto;

import com.clausetrail.model.Role;

public class AuthResponse {
    private String token;
    private String type;
    private String id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private String title;

    public AuthResponse() {}

    public AuthResponse(String token, String type, String id, String name, String email, Role role, String department, String title) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.title = title;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String type;
        private String id;
        private String name;
        private String email;
        private Role role;
        private String department;
        private String title;

        public Builder token(String token) { this.token = token; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder title(String title) { this.title = title; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, type, id, name, email, role, department, title);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
