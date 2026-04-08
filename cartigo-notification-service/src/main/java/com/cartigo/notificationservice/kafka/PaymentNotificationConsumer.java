package com.cartigo.notificationservice.kafka;

import com.cartigo.notificationservice.dto.PaymentNotificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PaymentNotificationConsumer {


    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public PaymentNotificationConsumer(JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"payment-success-topic", "payment-failed-topic"}, groupId = "notification-group")
    public void consumePaymentSuccessEvent(String payload) {
//        System.out.println("==================================================");
//        System.out.println("KAFKA NOTIFICATION TRIGGERED");
//        System.out.println("Payload recieved: "+ payload);

        try {
            PaymentNotificationDTO dto = objectMapper.readValue(payload, PaymentNotificationDTO.class);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(dto.getEmail());

            if ("SUCCESS".equalsIgnoreCase(dto.getStatus())) {
                message.setSubject("Payment Successful for Order: " + dto.getOrderId());
                message.setText("Dear Customer,\n\nYour payment for Order ID " + dto.getOrderId() + " was successful.\nReason: " + dto.getReason() + "\n\nThank you for shopping with CartiGo!");
            } else {
                message.setSubject("Payment Failed for Order: " + dto.getOrderId());
                message.setText("Dear Customer,\n\nYour payment for Order ID " + dto.getOrderId() + " failed.\nReason: " + dto.getReason() + "\n\nPlease try again!");
            }

            mailSender.send(message);
//            System.out.println("Email notification sent asynchronously to: " + dto.getEmail());

        } catch (Exception e) {
//            System.err.println("Failed to send email notification: " + e.getMessage());
        }
//        System.out.println("==================================================");
    }
}