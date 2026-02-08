package com.cartigo.returns.mapper;

import com.cartigo.returns.dto.RefundResponse;
import com.cartigo.returns.entity.Refund;

public class RefundMapper {

    private RefundMapper() {}

    public static RefundResponse toResponse(Refund r) {
        RefundResponse out = new RefundResponse();
        out.setId(r.getId());
        out.setReturnRequestId(r.getReturnRequestId());
        out.setAmount(r.getAmount());
        out.setStatus(r.getStatus());
        out.setGatewayRefundId(r.getGatewayRefundId());
        out.setCreatedAt(r.getCreatedAt());
        return out;
    }
}
