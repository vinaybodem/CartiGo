package com.cartigo.order.client;

import com.cartigo.order.client.fallback.PaymentClientFallback;
import com.cartigo.order.dto.PaymentRequest;
import com.cartigo.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE",configuration = FeignConfig.class,fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentResponse initiatePayment(@RequestBody PaymentRequest request);
}