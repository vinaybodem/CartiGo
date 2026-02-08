package com.cartigo.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateRazorpayOrderRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal amount; // in INR

    public CreateRazorpayOrderRequest() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
