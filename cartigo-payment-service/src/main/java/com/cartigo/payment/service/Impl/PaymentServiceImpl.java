package com.cartigo.payment.service.Impl;

import com.cartigo.payment.client.OrderClient;
import com.cartigo.payment.config.SecurityUtils;
import com.cartigo.payment.dto.PaymentRequest;
import com.cartigo.payment.dto.PaymentResponse;
import com.cartigo.payment.entity.Payment;
import com.cartigo.payment.enums.PaymentMethod;
import com.cartigo.payment.enums.PaymentStatus;
import com.cartigo.payment.repository.PaymentRepository;
import com.cartigo.payment.service.PaymentService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;

    public PaymentServiceImpl(
            PaymentRepository repository,
            OrderClient orderClient
    ) {
        this.repository = repository;
        this.orderClient = orderClient;
    }

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {

        /*
        ---------------------------------------
        CASE 1 : CASH ON DELIVERY
        ---------------------------------------
         */

        if (request.getPaymentMethod() == PaymentMethod.COD) {

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(SecurityUtils.getCurrentUserId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(PaymentMethod.COD);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            repository.save(payment);

            // Confirm order immediately
            orderClient.confirmOrder(request.getOrderId());

            PaymentResponse response = new PaymentResponse();
            response.setOrderId(request.getOrderId());
            response.setStatus(PaymentStatus.SUCCESS);

            return response;
        }

        /*
        ---------------------------------------
        CASE 2 : ONLINE PAYMENT (STRIPE)
        ---------------------------------------
         */
        System.out.println("Payment initiation request received for Order: " + request.getOrderId());
        try {

            long amountInPaise =
                    request.getAmount()
                            .multiply(new BigDecimal(100))
                            .longValue();

            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(SecurityUtils.getCurrentUserId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(PaymentMethod.ONLINE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setStripePaymentIntentId(intent.getId());

            repository.save(payment);

            PaymentResponse response = new PaymentResponse();
            response.setOrderId(request.getOrderId());
            response.setStatus(PaymentStatus.PENDING);
            response.setStripePaymentIntentId(intent.getId());
            response.setClientSecret(intent.getClientSecret());

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment initiation failed: " + e.getMessage());
        }
    }

    /*
    ---------------------------------------
    VERIFY / CONFIRM PAYMENT
    ---------------------------------------
     */

    @Override
    public PaymentResponse verifyPayment(String paymentIntentId) {

        Payment payment = repository
                .findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        repository.save(payment);

        // Confirm order after successful payment
        orderClient.confirmOrder(payment.getOrderId());

        PaymentResponse response = new PaymentResponse();
        response.setOrderId(payment.getOrderId());
        response.setStatus(PaymentStatus.SUCCESS);

        return response;
    }
}