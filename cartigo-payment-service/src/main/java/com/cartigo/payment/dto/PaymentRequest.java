package com.cartigo.payment.dto;

import com.cartigo.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long orderId;

    private Long userId;

    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;

    // getters setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal amount) {
        this.totalAmount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}