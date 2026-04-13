package com.cartigo.order.service.Impl;

import com.cartigo.order.client.CartClient;
import com.cartigo.order.client.InventoryClient;
import com.cartigo.order.client.NotificationClient;
import com.cartigo.order.client.PaymentClient;
import com.cartigo.order.dto.*;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderItem;
import com.cartigo.order.enums.OrderStatus;
import com.cartigo.order.repository.OrderItemRepository;
import com.cartigo.order.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private CartClient cartClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository itemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        AuthPrinciple principal = new AuthPrinciple(1L, "user@example.com", Collections.emptyList());

        lenient().when(authentication.getPrincipal()).thenReturn(principal);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createOrder_successful() {
        Long userId = 1L;
        CartResponse cart = new CartResponse();
        cart.setTotalAmount(BigDecimal.valueOf(100));

        Order savedOrder = new Order();
        savedOrder.setId(10L);
        savedOrder.setUserId(userId);
        savedOrder.setTotalAmount(BigDecimal.valueOf(100));
        savedOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(userId, cart);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_throwsException_whenPaymentFails() {
        setupSecurityContext(); // ✅ IMPORTANT

        Long userId = 1L;

        CartResponse cart = new CartResponse();
        cart.setTotalAmount(BigDecimal.valueOf(100));
        cart.setItems(List.of(new CartItemResponse())); // avoid empty cart

        when(cartClient.getCart()).thenReturn(cart);

        Order savedOrder = new Order();
        savedOrder.setId(10L);
        savedOrder.setUserId(userId);
        savedOrder.setTotalAmount(BigDecimal.valueOf(100));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("FAILED");
        when(paymentClient.initiatePayment(any())).thenReturn(paymentResponse);

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(userId));
    }

    @Test
    void getOrders_returnsOrderList() {
        Long userId = 1L;

        Order order = new Order();
        order.setId(10L);
        order.setUserId(userId);
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        OrderItem item = new OrderItem();
        item.setProductId(100L);
        when(itemRepository.findByOrderId(10L)).thenReturn(List.of(item));

        List<OrderResponse> result = orderService.getOrders(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getOrderId());
        assertEquals(OrderStatus.CONFIRMED, result.get(0).getStatus());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getOrder_returnsOrder_whenExists() {
        Long orderId = 10L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderItem item = new OrderItem();
        item.setProductId(100L);
        when(itemRepository.findByOrderId(orderId)).thenReturn(List.of(item));

        OrderResponse result = orderService.getOrder(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getOrder_throwsException_whenNotFound() {
        Long orderId = 10L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    void confirmOrder_successful() {
        Long orderId = 10L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //  mock cart
        CartResponse cart = new CartResponse();

        CartItemResponse item = new CartItemResponse();
        item.setProductId(1L);
        item.setQuantity(2);

        cart.setItems(List.of(item));

        when(cartClient.getCart()).thenReturn(cart);

        orderService.confirmOrder(orderId);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void confirmOrder_throwsException_whenNotFound() {
        Long orderId = 10L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.confirmOrder(orderId));
    }
}
