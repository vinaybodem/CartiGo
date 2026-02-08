package com.cartigo.common.dto.auth;

import com.cartigo.common.enums.Role;

public class AuthResponse {
    private Long userId;
    private String email;
    private Role role;
    private String token;

    public AuthResponse() {}

    public AuthResponse(Long userId, String email, Role role, String token) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
