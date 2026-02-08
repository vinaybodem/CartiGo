package com.cartigo.returns.service;

import com.cartigo.returns.dto.ReturnCreateRequest;
import com.cartigo.returns.dto.ReturnDecisionRequest;
import com.cartigo.returns.entity.Refund;
import com.cartigo.returns.entity.RefundStatus;
import com.cartigo.returns.entity.ReturnRequest;
import com.cartigo.returns.entity.ReturnStatus;
import com.cartigo.returns.exception.BadRequestException;
import com.cartigo.returns.exception.ResourceNotFoundException;
import com.cartigo.returns.repository.RefundRepository;
import com.cartigo.returns.repository.ReturnRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReturnRefundService {

    private final ReturnRequestRepository returnRepo;
    private final RefundRepository refundRepo;
    private final PurchaseVerifier purchaseVerifier;

    public ReturnRefundService(ReturnRequestRepository returnRepo,
                               RefundRepository refundRepo,
                               PurchaseVerifier purchaseVerifier) {
        this.returnRepo = returnRepo;
        this.refundRepo = refundRepo;
        this.purchaseVerifier = purchaseVerifier;
    }

    @Transactional
    public ReturnRequest createReturn(ReturnCreateRequest req) {

        if (!purchaseVerifier.hasPurchased(req.getUserId(), req.getProductId())) {
            throw new BadRequestException(
                    "Only customers who ordered this product can request return/refund"
            );
        }

        returnRepo.findByOrderIdAndProductId(req.getOrderId(), req.getProductId())
                .ifPresent(r -> {
                    throw new BadRequestException(
                            "Return request already exists for this order item"
                    );
                });

        ReturnRequest rr = new ReturnRequest();
        rr.setOrderId(req.getOrderId());
        rr.setProductId(req.getProductId());
        rr.setUserId(req.getUserId());
        rr.setReason(req.getReason());
        rr.setRefundAmount(req.getRefundAmount());
        rr.setStatus(ReturnStatus.REQUESTED);

        return returnRepo.save(rr);
    }

    public ReturnRequest getReturn(Long returnId) {
        return returnRepo.findById(returnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Return request not found: " + returnId)
                );
    }

    public List<ReturnRequest> listByUser(Long userId) {
        return returnRepo.findByUserId(userId);
    }

    public List<ReturnRequest> listAll() {
        return returnRepo.findAll();
    }

    @Transactional
    public ReturnRequest approve(Long returnId, ReturnDecisionRequest req) {

        ReturnRequest rr = getReturn(returnId);

        if (rr.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException(
                    "Return can be approved only when status is REQUESTED"
            );
        }

        // Step 1: approve
        rr.setStatus(ReturnStatus.APPROVED);
        rr.setReviewedAt(LocalDateTime.now());
        rr.setReviewedByAdminId(req.getAdminId());
        rr = returnRepo.save(rr);

        Refund refund = refundRepo.findByReturnRequestId(returnId).orElse(null);

        if (refund == null) {
            refund = new Refund();
            refund.setReturnRequestId(returnId);
            refund.setAmount(rr.getRefundAmount());
            refund.setStatus(RefundStatus.PENDING);
            refundRepo.save(refund);
        }

        // Step 3: mark refund initiated
        rr.setStatus(ReturnStatus.REFUND_INITIATED);
        return returnRepo.save(rr);
    }

    // ================= REJECT =================
    @Transactional
    public ReturnRequest reject(Long returnId, ReturnDecisionRequest req) {

        ReturnRequest rr = getReturn(returnId);

        if (rr.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException(
                    "Return can be rejected only when status is REQUESTED"
            );
        }

        rr.setStatus(ReturnStatus.REJECTED);
        rr.setReviewedAt(LocalDateTime.now());
        rr.setReviewedByAdminId(req.getAdminId());

        return returnRepo.save(rr);
    }

    // ================= REFUND =================
    public Refund getRefundByReturnId(Long returnId) {
        return refundRepo.findByReturnRequestId(returnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Refund not found for returnRequestId " + returnId
                        )
                );
    }

    @Transactional
    public Refund markRefundSuccess(Long returnId, String gatewayRefundId) {

        Refund refund = getRefundByReturnId(returnId);
        refund.setStatus(RefundStatus.SUCCESS);
        refund.setGatewayRefundId(gatewayRefundId);
        refundRepo.save(refund);

        ReturnRequest rr = getReturn(returnId);
        rr.setStatus(ReturnStatus.REFUNDED);
        returnRepo.save(rr);

        return refund;
    }

    @Transactional
    public Refund markRefundFailed(Long returnId, String gatewayRefundId) {

        Refund refund = getRefundByReturnId(returnId);
        refund.setStatus(RefundStatus.FAILED);
        refund.setGatewayRefundId(gatewayRefundId);

        return refundRepo.save(refund);
    }
}
