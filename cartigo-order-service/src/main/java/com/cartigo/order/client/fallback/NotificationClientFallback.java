package com.cartigo.order.client.fallback;

import com.cartigo.order.client.NotificationClient;
import com.cartigo.order.dto.NotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public void sendEmail(NotificationRequest request) {
        System.err.println("Notification Service is unavailable. Circuit breaker open.");
    }
}
