package com.cartigo.order.dto;

import com.cartigo.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private String paymentIntentId;
    private String paymentStatus;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    private List<OrderItemResponse> items;

    // getters setters


    public OrderResponse() {
    }

    public OrderResponse(Long orderId, BigDecimal totalAmount, OrderStatus status, LocalDateTime createdAt, String paymentIntentId, String paymentStatus, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.paymentIntentId = paymentIntentId;
        this.paymentStatus = paymentStatus;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
