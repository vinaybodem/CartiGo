package com.cartigo.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal totalAmount;

    @Valid
    @NotNull
    @Size(min = 1)
    private List<OrderItemCreateRequest> items;

    public OrderCreateRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItemCreateRequest> getItems() { return items; }
    public void setItems(List<OrderItemCreateRequest> items) { this.items = items; }
}
