package com.cartigo.notificationservice.service;

import com.cartigo.notificationservice.dto.NotificationRequest;

public interface NotificationService {

    void sendMail(NotificationRequest request);
}
