package com.cartigo.order.kafka;

import com.cartigo.order.client.CartClient;
import com.cartigo.order.client.InventoryClient;
import com.cartigo.order.dto.AddToCartRequest;
import com.cartigo.order.dto.PaymentNotificationDTO;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderItem;
import com.cartigo.order.enums.OrderStatus;
import com.cartigo.order.repository.OrderItemRepository;
import com.cartigo.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentFailedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryClient inventoryClient;
    private final CartClient cartClient;

    public PaymentFailedConsumer(ObjectMapper objectMapper, OrderRepository orderRepository, OrderItemRepository orderItemRepository, InventoryClient inventoryClient, CartClient cartClient) {
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryClient = inventoryClient;
        this.cartClient = cartClient;
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    public void consumePaymentFailedEvent(String payload) {
        try {
            PaymentNotificationDTO dto = objectMapper.readValue(payload, PaymentNotificationDTO.class);

            if (dto.getOrderId() != null && dto.getOrderId() != 0) {
                Order order = orderRepository.findById(dto.getOrderId()).orElse(null);
                if (order != null) {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);

                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    for (OrderItem item : items) {
                        try {
                            inventoryClient.release(item.getProductId(), item.getQuantity());
                        } catch(Exception e) {
                            System.err.println("Failed to release inventory: " + e.getMessage());
                        }

                        try {
                            cartClient.addItem(new AddToCartRequest(item.getProductId(), item.getQuantity()));
                        } catch(Exception e) {
                            System.err.println("Failed to repopulate cart: " + e.getMessage());
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to process payment failure event in Order Service: " + e.getMessage());
        }
    }
}
