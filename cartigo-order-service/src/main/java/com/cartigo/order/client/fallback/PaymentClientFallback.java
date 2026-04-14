package com.cartigo.order.client.fallback;

import com.cartigo.order.client.PaymentClient;
import com.cartigo.order.dto.PaymentRequest;
import com.cartigo.order.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallback implements PaymentClient {
    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
        throw new RuntimeException("Payment Service is temporarily unavailable. Circuit breaker open.");
    }
}
