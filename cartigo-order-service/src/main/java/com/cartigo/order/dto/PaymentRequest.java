package com.cartigo.order.dto;

import com.cartigo.order.enums.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long orderId;

    private Long userId;

    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
