package com.cartigo.cart.client.fallback;

import com.cartigo.cart.client.NotificationClient;
import com.cartigo.cart.dto.NotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public void sendEmail(NotificationRequest request) {
        // Log the failure or ignore since it's just a notification
        System.err.println("Notification Service is unavailable. Circuit breaker open.");
    }
}
