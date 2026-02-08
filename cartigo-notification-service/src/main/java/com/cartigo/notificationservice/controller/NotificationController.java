package com.cartigo.notificationservice.controller;

import com.cartigo.common.dto.ApiResponse;
import com.cartigo.notificationservice.dto.NotificationRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @PostMapping("/email")
    public ApiResponse<Object> email(@Valid @RequestBody NotificationRequest req) {
        // Stub: just log. Replace with Spring Mail/Twilio/WhatsApp provider.
        log.info("NOTIFY to={} subject={} message={}", req.getTo(), req.getSubject(), req.getMessage());
        return ApiResponse.ok("Notification queued (stub)", null);
    }
}
