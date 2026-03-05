package com.cartigo.cart.client;

import com.cartigo.cart.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/notification/email")
    void sendEmail(NotificationRequest request);
}
