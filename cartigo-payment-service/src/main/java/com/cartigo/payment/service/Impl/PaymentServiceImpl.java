package com.cartigo.payment.service.Impl;

import com.cartigo.payment.client.OrderClient;
import com.cartigo.payment.config.SecurityUtils;
import com.cartigo.payment.dto.PaymentNotificationDTO;
import com.cartigo.payment.dto.PaymentRequest;
import com.cartigo.payment.dto.PaymentResponse;
import com.cartigo.payment.dto.PaymentVerifyRequest;
import com.cartigo.payment.entity.Payment;
import com.cartigo.payment.enums.PaymentMethod;
import com.cartigo.payment.enums.PaymentStatus;
import com.cartigo.payment.repository.PaymentRepository;
import com.cartigo.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;
    private final RazorpayClient razorpayClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentServiceImpl(
            PaymentRepository repository,
            OrderClient orderClient, RazorpayClient razorpayClient, KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.repository = repository;
        this.orderClient = orderClient;
        this.razorpayClient = razorpayClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {

        if (request.getPaymentMethod() == PaymentMethod.COD) {

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(SecurityUtils.getCurrentUserId());
            payment.setTotalAmount(request.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.COD);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            repository.save(payment);

            // Confirm order immediately
            orderClient.confirmOrder(request.getOrderId());
            // Send notification via Kafka
            try {
                PaymentNotificationDTO notification = new PaymentNotificationDTO();
                notification.setOrderId(payment.getOrderId());
                notification.setPaymentId(null);
                notification.setStatus("SUCCESS");
                notification.setReason("Cash On Delivery");
                notification.setEmail(SecurityUtils.getCurrentEmail());

                String payload = new ObjectMapper().writeValueAsString(notification);
                kafkaTemplate.send("payment-success-topic", payload);
                System.out.println("Notification Sent");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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
        try {
            long amountInPaise = request.getTotalAmount().multiply(new BigDecimal(100)).longValue();

            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + request.getOrderId());
            Order order = razorpayClient.orders.create(options);

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(SecurityUtils.getCurrentUserId());
            payment.setTotalAmount(request.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.ONLINE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setRazorpayOrderId(order.get("id"));

            repository.save(payment);

            PaymentResponse response = new PaymentResponse();
            response.setOrderId(request.getOrderId());
            response.setStatus(PaymentStatus.PENDING);
            response.setRazorpayOrderId(order.get("id"));
            response.setTotalAmount(String.valueOf(amountInPaise));

            return response;

        } catch (Exception e) {
            PaymentNotificationDTO notification = new PaymentNotificationDTO();
            notification.setOrderId(request.getOrderId());
            notification.setStatus("FAILED");
            notification.setReason("Payment initiation failed");
            notification.setEmail(SecurityUtils.getCurrentEmail());
            try {
                String payload = new ObjectMapper().writeValueAsString(notification);
                kafkaTemplate.send("payment-failed-topic", payload);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Razorpay payment initiation failed: " + e.getMessage());

        }
    }

    /*
    ---------------------------------------
    VERIFY / CONFIRM PAYMENT
    ---------------------------------------
     */

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

            if (!isValid) {
                throw new RuntimeException("Payment signature verification failed");
            }

            Payment payment = repository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            repository.save(payment);

            // Confirm order after successful payment
            orderClient.confirmOrder(payment.getOrderId());

            try {
                PaymentNotificationDTO notification = new PaymentNotificationDTO();
                notification.setOrderId(payment.getOrderId());
                notification.setPaymentId(payment.getRazorpayPaymentId());
                notification.setStatus("SUCCESS");
                notification.setReason("Online Payment Successful");
                notification.setEmail(SecurityUtils.getCurrentEmail());

                String payload = new ObjectMapper().writeValueAsString(notification);
                kafkaTemplate.send("payment-success-topic", payload);
                System.out.println("Notification sent");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(payment.getOrderId());
            response.setStatus(PaymentStatus.SUCCESS);

            return response;
        } catch (Exception e) {
            PaymentNotificationDTO notification = new PaymentNotificationDTO();
            // Best effort to find orderId
            repository.findByRazorpayOrderId(request.getRazorpayOrderId()).ifPresent(p -> {
                notification.setOrderId(p.getOrderId());
                notification.setEmail(SecurityUtils.getCurrentEmail());
            });
            notification.setStatus("FAILED");
            notification.setReason("Payment Verification Failed: " + e.getMessage());

            try {
                String payload = new ObjectMapper().writeValueAsString(notification);
                kafkaTemplate.send("payment-failed-topic", payload);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
}