package com.cartigo.order.client;

import com.cartigo.order.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/notification/email")
    void sendEmail(@RequestBody NotificationRequest request);
}
