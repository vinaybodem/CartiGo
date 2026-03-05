package com.cartigo.notificationservice.controller;

import com.cartigo.common.dto.ApiResponse;
import com.cartigo.notificationservice.dto.NotificationRequest;
import com.cartigo.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<Object> email(@Valid @RequestBody NotificationRequest req) {
        // Stub: just log. Replace with Spring Mail/Twilio/WhatsApp provider.

        notificationService.sendMail(req);
        return ResponseEntity.ok("Email sent Successfully");
    }
}
