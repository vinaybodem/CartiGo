package com.cartigo.notificationservice.kafka;

import com.cartigo.notificationservice.dto.OrderNotificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationConsumer {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public OrderNotificationConsumer(JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"order-success-topic", "order-failed-topic"}, groupId = "notification-group")
    public void consumeOrderEvent(String payload) {
        try {
            OrderNotificationDTO dto = objectMapper.readValue(payload, OrderNotificationDTO.class);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(dto.getEmail());

            if ("SUCCESS".equalsIgnoreCase(dto.getStatus())) {
                message.setSubject("Order Confirmed: #" + dto.getOrderId());
                message.setText("Dear Customer,\n\nYour order #" + dto.getOrderId() + " has been successfully confirmed.\n" +
                        "Reason: " + dto.getReason() + "\n\nThank you for shopping with CartiGo!");
            } else {
                message.setSubject("Order Failed" + (dto.getOrderId() != null ? ": #" + dto.getOrderId() : ""));
                message.setText("Dear Customer,\n\nWe were unable to process your order.\n" +
                        "Reason: " + dto.getReason() + "\n\nPlease try again or contact support.");
            }

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
