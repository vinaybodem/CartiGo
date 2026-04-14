package com.cartigo.order.client;

import com.cartigo.order.client.fallback.NotificationClientFallback;
import com.cartigo.order.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE",fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping("/api/notification/email")
    public abstract void sendEmail(@RequestBody NotificationRequest request);
}
