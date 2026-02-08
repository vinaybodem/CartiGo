package com.cartigo.returns.service;

import com.cartigo.returns.integration.OrderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignPurchaseVerifier implements PurchaseVerifier {

    private final OrderClient orderClient;

    @Value("${order.service.enabled:true}")
    private boolean enabled;

    /**
     * failOpen=false (recommended): if order-service is down, do NOT allow returns.
     * set true only for dev.
     */
    @Value("${order.service.fail-open:false}")
    private boolean failOpen;

    public FeignPurchaseVerifier(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public boolean hasPurchased(Long userId, Long productId) {
        if (!enabled) return true;

        try {
            Boolean result = orderClient.verifyPurchase(userId, productId);
            return result != null && result;
        } catch (Exception ex) {
            return failOpen;
        }
    }
}
