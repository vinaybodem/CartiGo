package com.cartigo.payment.controller;

import com.cartigo.payment.dto.PaymentRequest;
import com.cartigo.payment.dto.PaymentResponse;
import com.cartigo.payment.dto.PaymentVerifyRequest;
import com.cartigo.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/initiate")
    public PaymentResponse initiate(@RequestBody PaymentRequest request){

        return service.initiatePayment(request);
    }

    @PostMapping("/verify")
    public PaymentResponse verify(
            @RequestBody PaymentVerifyRequest request
    ){

        return service.verifyPayment(request);
    }
}