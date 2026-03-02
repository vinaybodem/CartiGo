package com.cartigo.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cartigo-user-service")
public interface SellerClient {

    @GetMapping("/sellers/{id}/validate")
    boolean isSellerValid(@PathVariable Long id);
}
