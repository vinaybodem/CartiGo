package com.cartigo.review.service;

import com.cartigo.review.integration.OrderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignPurchaseVerifier implements PurchaseVerifier {

    private final OrderClient orderClient;

    @Value("${order.service.enabled:true}")
    private boolean enabled;

    // ✅ safer default: if order service fails, block review (recommended)
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
            // ✅ if failOpen=true -> allow review even if order service is down
            // ✅ if failOpen=false -> block review to prevent fake reviews
            return failOpen;
        }
    }
}
