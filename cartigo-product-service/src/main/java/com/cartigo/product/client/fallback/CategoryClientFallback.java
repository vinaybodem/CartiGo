package com.cartigo.product.client.fallback;

import com.cartigo.product.client.CategoryClient;
import org.springframework.stereotype.Component;

@Component
public class CategoryClientFallback implements CategoryClient {
    @Override
    public boolean isCategoryValid(Long category_id) {
        // Return false as a safety measure if category service is down
        return false;
    }

    @Override
    public Long getCategroyId(String category_name) {
        throw new RuntimeException("Category Service is temporarily unavailable. Circuit breaker open.");
    }
}
