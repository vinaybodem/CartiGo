package com.cartigo.order.client;

import com.cartigo.order.dto.PaymentRequest;
import com.cartigo.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE",configuration = FeignConfig.class)
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentResponse initiatePayment(@RequestBody PaymentRequest request);
}