package com.cartigo.inventoryservice.client;

import com.cartigo.inventoryservice.dao.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/products/validate/{prod_id}")
    ProductResponse validate(@PathVariable Long prod_id);
}
