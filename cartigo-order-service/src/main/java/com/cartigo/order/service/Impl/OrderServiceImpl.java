package com.cartigo.order.service.Impl;


import com.cartigo.order.client.CartClient;
import com.cartigo.order.client.InventoryClient;
import com.cartigo.order.client.PaymentClient;
import com.cartigo.order.config.SecurityUtils;
import com.cartigo.order.dto.*;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderItem;
import com.cartigo.order.enums.OrderStatus;
import com.cartigo.order.enums.PaymentMethod;
import com.cartigo.order.repository.OrderItemRepository;
import com.cartigo.order.repository.OrderRepository;
import com.cartigo.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
   // private final NotificationClient notificationClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(
            CartClient cartClient,
            InventoryClient inventoryClient, PaymentClient paymentClient,
            OrderRepository orderRepository,
            OrderItemRepository itemRepository, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper
    ) {
        this.cartClient = cartClient;
        this.inventoryClient = inventoryClient;
//        this.notificationClient = notificationClient;
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(Long userId, CartResponse cart) {

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        return orderRepository.save(order);
    }
    @Override

    public OrderResponse placeOrder(Long userId) {

        Order savedOrder = null;
        try{
        // Validate checkout
        cartClient.validateCheckout();

        //  Fetch cart
        CartResponse cart = cartClient.getCart();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        savedOrder= createOrder(userId,cart);


        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(savedOrder.getId());
        paymentRequest.setUserId(userId);
        paymentRequest.setTotalAmount(savedOrder.getTotalAmount());
        paymentRequest.setPaymentMethod(PaymentMethod.ONLINE); // or ONLINE

        System.out.println("Calling Payment Service for Order: " + savedOrder.getId());
            PaymentResponse paymentResponse = paymentClient.initiatePayment(paymentRequest);
            if(paymentResponse.getStatus().equals("FAILED")){
                throw new RuntimeException("Payment failed");
            }
        System.out.println("Payment Response: " + paymentResponse);



//
//        //  Prepare list of order item responses
//        List<OrderItemResponse> orderItems = new ArrayList<>();
//
//        // Process each cart item
//        for (CartItemResponse item : cart.getItems()) {
//
//            // Reserve stock
//            inventoryClient.reserve(
//                    item.getProductId(),
//                    item.getQuantity()
//            );
//
//            // Create OrderItem
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrderId(savedOrder.getId());
//            orderItem.setProductId(item.getProductId());
//            orderItem.setProductName(item.getProductName());
//            orderItem.setImageUrl(item.getImageUrl());
//            orderItem.setPrice(item.getUnitPrice());
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setLineTotal(item.getLineTotal());
//
//            OrderItem savedItem = itemRepository.save(orderItem);
//
//            // Reduce inventory
////            inventoryClient.reduce(
////                    item.getProductId(),
////                    item.getQuantity()
////            );
//
//            // Map to response
//            orderItems.add(itemResponseMapper(savedItem));
//        }
//
//        //  Clear cart
//        cartClient.clearCart();
//
//        // Send notification
//        NotificationRequest req = new NotificationRequest();
//        req.setTo(SecurityUtils.getCurrentEmail());
//        req.setSubject("Order Placed");
//        req.setMessage("Your order #" + savedOrder.getId() + " has been placed.");
//
//        notificationClient.sendEmail(req);

        // 🔹 Step 5: Return response to frontend
        OrderResponse response = new OrderResponse();
        response.setOrderId(savedOrder.getId());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setStatus(savedOrder.getStatus()); // PENDING_PAYMENT
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setItems(new ArrayList<>());

        // Razorpay details (frontend needs this)
        response.setPaymentStatus(paymentResponse.getStatus());
        response.setRazorpayOrderId(paymentResponse.getRazorpayOrderId());
        response.setTotalAmount(savedOrder.getTotalAmount());

        return response;
    }catch (Exception e) {
            boolean isPaymentFailure = e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("payment");

            if (!isPaymentFailure) {
                try {
                    OrderNotificationDTO notification = new OrderNotificationDTO();
                    notification.setOrderId(savedOrder != null ? savedOrder.getId() : null);
                    notification.setEmail(SecurityUtils.getCurrentEmail());
                    notification.setStatus("FAILED");
                    notification.setReason("Order placement failed: " + e.getMessage());

                    String payload = objectMapper.writeValueAsString(notification);
                    kafkaTemplate.send("order-failed-topic", payload);
                    System.out.println("Notification sent for order failure.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        }
    }

    @Override
    public List<OrderResponse> getOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(responseMapper(order));
        }
        return orderResponses;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order Not Found"));

        return responseMapper(order);
    }

    @Transactional
    public void confirmOrder(Long orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return;
        }
        // 🔹 Step 2: Fetch cart (or ideally snapshot)
        CartResponse cart = cartClient.getCart();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty during confirmation");
        }

        // 🔹 Step 3: Save order items + reduce inventory
        for (CartItemResponse item : cart.getItems()) {

            // Save order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setImageUrl(item.getImageUrl());
            orderItem.setPrice(item.getUnitPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setLineTotal(item.getLineTotal());

            itemRepository.save(orderItem);

            // Reduce inventory
            inventoryClient.reduce(item.getProductId(), item.getQuantity());
        }

        // 🔹 Step 4: Update order status
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // 🔹 Step 5: Clear cart AFTER success
        cartClient.clearCart();

        // 🔹 Step 6: Send notification
        try {
            OrderNotificationDTO notification = new OrderNotificationDTO();
            notification.setOrderId(orderId);
            notification.setEmail(SecurityUtils.getCurrentEmail());
            notification.setStatus("SUCCESS");
            notification.setReason("Your order has been confirmed successfully");

            String payload = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send("order-success-topic", payload);
            System.out.println("Notification sent for order confirmation.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public OrderResponse  responseMapper(Order order){
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setCreatedAt(order.getCreatedAt());
        List<OrderItem> orderItems = itemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            orderItemResponses.add(itemResponseMapper(orderItem));
        }
        orderResponse.setItems(orderItemResponses);
        return orderResponse;
    }

    public OrderItemResponse  itemResponseMapper(OrderItem orderItem){
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setProductId(orderItem.getProductId());
        orderItemResponse.setProductName(orderItem.getProductName());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPrice(orderItem.getPrice());
        orderItemResponse.setImageUrl(orderItem.getImageUrl());
        orderItemResponse.setLineTotal(orderItem.getLineTotal());
        return orderItemResponse;
    }
}