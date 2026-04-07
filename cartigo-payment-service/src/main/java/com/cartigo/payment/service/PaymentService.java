package com.cartigo.payment.service;

import com.cartigo.payment.dto.PaymentRequest;
import com.cartigo.payment.dto.PaymentResponse;
import com.cartigo.payment.dto.PaymentVerifyRequest;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentResponse verifyPayment(PaymentVerifyRequest request);

}