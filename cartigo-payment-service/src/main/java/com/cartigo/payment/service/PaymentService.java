package com.cartigo.payment.service;

import com.cartigo.payment.dto.CreateRazorpayOrderRequest;
import com.cartigo.payment.dto.CreateRefundRequest;
import com.cartigo.payment.dto.VerifyPaymentRequest;
import com.cartigo.payment.entity.Payment;
import com.cartigo.payment.entity.PaymentStatus;
import com.cartigo.payment.entity.Refund;
import com.cartigo.payment.entity.RefundStatus;
import com.cartigo.payment.exception.BadRequestException;
import com.cartigo.payment.exception.ResourceNotFoundException;
import com.cartigo.payment.repository.PaymentRepository;
import com.cartigo.payment.repository.RefundRepository;
import com.cartigo.payment.util.RazorpaySignatureUtil;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    public PaymentService(RazorpayClient razorpayClient,
                          PaymentRepository paymentRepository,
                          RefundRepository refundRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    @Transactional
    public Payment createRazorpayOrder(CreateRazorpayOrderRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be > 0");
        }

        // Save initial payment record
        Payment p = new Payment();
        p.setOrderId(req.getOrderId());
        p.setUserId(req.getUserId());
        p.setAmount(req.getAmount());
        p.setStatus(PaymentStatus.CREATED);
        p = paymentRepository.save(p);

        int amountPaise = toPaise(req.getAmount());

        JSONObject options = new JSONObject();
        options.put("amount", amountPaise);
        options.put("currency", "INR");
        options.put("receipt", "cartigo_order_" + req.getOrderId());

        try {
            com.razorpay.Order rzOrder = razorpayClient.orders.create(options);
            p.setRazorpayOrderId(rzOrder.get("id"));
            return paymentRepository.save(p);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Transactional
    public Payment verifyAndMarkPaid(VerifyPaymentRequest req) {
        Payment p = paymentRepository.findById(req.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + req.getPaymentId()));

        // sanity check
        if (p.getRazorpayOrderId() == null || !p.getRazorpayOrderId().equals(req.getRazorpayOrderId())) {
            throw new BadRequestException("Razorpay orderId mismatch");
        }

        boolean ok = RazorpaySignatureUtil.verify(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature(),
                razorpaySecret
        );

        p.setRazorpayPaymentId(req.getRazorpayPaymentId());
        p.setRazorpaySignature(req.getRazorpaySignature());
        p.setStatus(ok ? PaymentStatus.PAID : PaymentStatus.FAILED);

        return paymentRepository.save(p);
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }

    public List<Refund> getRefunds(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }

    @Transactional
    public Refund createRefund(CreateRefundRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Refund amount must be > 0");
        }

        Payment p = getPayment(req.getPaymentId());

        if (p.getStatus() != PaymentStatus.PAID || p.getRazorpayPaymentId() == null) {
            throw new BadRequestException("Refund allowed only for PAID payments with razorpayPaymentId");
        }

        // create DB refund row first (audit)
        Refund r = new Refund();
        r.setPaymentId(p.getId());
        r.setAmount(req.getAmount());
        r.setStatus(RefundStatus.PENDING);
        r = refundRepository.save(r);

        try {
            JSONObject refundReq = new JSONObject();
            refundReq.put("amount", toPaise(req.getAmount()));

            // ✅ FIX: refund is via razorpayClient.payments.refund(paymentId, json)
            com.razorpay.Refund rzRefund = razorpayClient.payments.refund(p.getRazorpayPaymentId(), refundReq); // <-- FIX

            r.setRazorpayRefundId(rzRefund.get("id"));
            r.setStatus(RefundStatus.SUCCESS); // if call didn't throw, treat as success
            return refundRepository.save(r);

        } catch (Exception e) {
            r.setStatus(RefundStatus.FAILED);
            refundRepository.save(r);
            throw new BadRequestException("Refund failed: " + e.getMessage());
        }
    }

    private int toPaise(BigDecimal inr) {
        BigDecimal v = inr.setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        return v.intValueExact();
    }
}
