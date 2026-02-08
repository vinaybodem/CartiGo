package com.cartigo.payment.mapper;

import com.cartigo.payment.dto.PaymentResponse;
import com.cartigo.payment.entity.Payment;

public class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setOrderId(p.getOrderId());
        r.setUserId(p.getUserId());
        r.setAmount(p.getAmount());
        r.setStatus(p.getStatus());
        r.setRazorpayOrderId(p.getRazorpayOrderId());
        r.setRazorpayPaymentId(p.getRazorpayPaymentId());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
