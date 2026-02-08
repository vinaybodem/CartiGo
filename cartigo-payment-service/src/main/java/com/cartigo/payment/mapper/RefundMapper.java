package com.cartigo.payment.mapper;

import com.cartigo.payment.dto.RefundResponse;
import com.cartigo.payment.entity.Refund;

public class RefundMapper {

    private RefundMapper() {}

    public static RefundResponse toResponse(Refund r) {
        RefundResponse out = new RefundResponse();
        out.setId(r.getId());
        out.setPaymentId(r.getPaymentId());
        out.setAmount(r.getAmount());
        out.setStatus(r.getStatus());
        out.setRazorpayRefundId(r.getRazorpayRefundId());
        out.setCreatedAt(r.getCreatedAt());
        return out;
    }
}
