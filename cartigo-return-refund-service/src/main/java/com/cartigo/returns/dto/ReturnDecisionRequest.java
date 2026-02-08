package com.cartigo.returns.dto;

import jakarta.validation.constraints.NotNull;

public class ReturnDecisionRequest {

    @NotNull
    private Long adminId;

    public ReturnDecisionRequest() {}

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}
