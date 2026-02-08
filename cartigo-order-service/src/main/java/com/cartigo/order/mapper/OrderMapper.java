package com.cartigo.order.mapper;

import com.cartigo.order.dto.OrderItemResponse;
import com.cartigo.order.dto.OrderResponse;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {}

    public static OrderItemResponse toItem(OrderItem i) {
        OrderItemResponse r = new OrderItemResponse();
        r.setId(i.getId());
        r.setProductId(i.getProductId());
        r.setQuantity(i.getQuantity());
        r.setProductName(i.getProductName());
        r.setImageUrl(i.getImageUrl());
        r.setUnitPrice(i.getUnitPrice());
        return r;
    }

    public static OrderResponse toOrder(Order o, List<OrderItem> items) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUserId());
        r.setTotalAmount(o.getTotalAmount());
        r.setStatus(o.getStatus());
        r.setCreatedAt(o.getCreatedAt());
        r.setItems(items.stream().map(OrderMapper::toItem).toList());
        return r;
    }
}
