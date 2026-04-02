package com.cartigo.order.service;

import com.cartigo.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(Long userId);

    List<OrderResponse> getOrders(Long userId);

    OrderResponse getOrder(Long orderId);

    public void confirmOrder(Long orderId);
}