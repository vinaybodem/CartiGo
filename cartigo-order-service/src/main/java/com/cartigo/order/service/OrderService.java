package com.cartigo.order.service;

import com.cartigo.order.dto.OrderCreateRequest;
import com.cartigo.order.dto.OrderItemCreateRequest;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderItem;
import com.cartigo.order.entity.OrderStatus;
import com.cartigo.order.exception.BadRequestException;
import com.cartigo.order.exception.ResourceNotFoundException;
import com.cartigo.order.repository.OrderItemRepository;
import com.cartigo.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Order create(OrderCreateRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BadRequestException("Order items required");
        }

        Order o = new Order();
        o.setUserId(req.getUserId());
        o.setTotalAmount(req.getTotalAmount());
        o.setStatus(OrderStatus.PLACED);
        o = orderRepository.save(o);

        for (OrderItemCreateRequest it : req.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(o.getId());
            oi.setProductId(it.getProductId());
            oi.setQuantity(it.getQuantity());
            oi.setProductName(it.getProductName());
            oi.setImageUrl(it.getImageUrl());
            oi.setUnitPrice(it.getUnitPrice());
            itemRepository.save(oi);
        }

        return o;
    }

    public Order get(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    public List<Order> listByUser(Long userId) {
        return orderRepository.findByUserIdOrderByIdDesc(userId);
    }

    public List<OrderItem> items(Long orderId) {
        return itemRepository.findByOrderId(orderId);
    }

    @Transactional
    public Order setStatus(Long orderId, OrderStatus status) {
        Order o = get(orderId);
        o.setStatus(status);
        return orderRepository.save(o);
    }

    public boolean verifyPurchase(Long userId, Long productId) {
        return itemRepository.existsDeliveredPurchase(userId, productId);
    }
}
