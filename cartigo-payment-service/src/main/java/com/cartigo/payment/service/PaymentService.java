package com.cartigo.payment.service;

import com.cartigo.payment.dto.PaymentRequest;
import com.cartigo.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentResponse verifyPayment(String paymentIntentId);

}