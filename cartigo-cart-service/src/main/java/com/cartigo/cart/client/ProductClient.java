package com.cartigo.cart.client;

import com.cartigo.cart.client.fallback.ProductClientFallback;
import com.cartigo.cart.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/products/cart/{prod_id}")
    ProductResponse getProduct(@PathVariable Long prod_id);

}
