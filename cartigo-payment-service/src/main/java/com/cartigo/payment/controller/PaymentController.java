package com.cartigo.payment.controller;

import com.cartigo.payment.dto.*;
import com.cartigo.payment.mapper.PaymentMapper;
import com.cartigo.payment.mapper.RefundMapper;
import com.cartigo.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/razorpay/order")
    public ResponseEntity<CreateRazorpayOrderResponse> createRazorpayOrder(@Valid @RequestBody CreateRazorpayOrderRequest req) {
        var p = service.createRazorpayOrder(req);

        CreateRazorpayOrderResponse out = new CreateRazorpayOrderResponse();
        out.setPaymentId(p.getId());
        out.setRazorpayOrderId(p.getRazorpayOrderId());
        out.setCurrency("INR");

        out.setAmountPaise(toPaise(req.getAmount()));

        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<PaymentResponse> verify(@Valid @RequestBody VerifyPaymentRequest req) {
        return ResponseEntity.ok(PaymentMapper.toResponse(service.verifyAndMarkPaid(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(PaymentMapper.toResponse(service.getPayment(id)));
    }

    @PostMapping("/razorpay/refund")
    public ResponseEntity<RefundResponse> refund(@Valid @RequestBody CreateRefundRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(RefundMapper.toResponse(service.createRefund(req)));
    }

    @GetMapping("/{paymentId}/refunds")
    public ResponseEntity<List<RefundResponse>> listRefunds(@PathVariable Long paymentId) {
        var out = service.getRefunds(paymentId).stream().map(RefundMapper::toResponse).toList();
        return ResponseEntity.ok(out);
    }

    private int toPaise(BigDecimal inr) {
        BigDecimal v = inr.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        return v.intValueExact();
    }
}
