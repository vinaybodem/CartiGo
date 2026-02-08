package com.cartigo.returns.mapper;

import com.cartigo.returns.dto.ReturnResponse;
import com.cartigo.returns.entity.ReturnRequest;

public class ReturnMapper {

    private ReturnMapper() {}

    public static ReturnResponse toResponse(ReturnRequest r) {
        ReturnResponse out = new ReturnResponse();
        out.setId(r.getId());
        out.setOrderId(r.getOrderId());
        out.setProductId(r.getProductId());
        out.setUserId(r.getUserId());
        out.setReason(r.getReason());
        out.setRefundAmount(r.getRefundAmount());
        out.setStatus(r.getStatus());
        out.setRequestedAt(r.getRequestedAt());
        out.setReviewedAt(r.getReviewedAt());
        out.setReviewedByAdminId(r.getReviewedByAdminId());
        return out;
    }
}
