package com.cartigo.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "cartigo-category-service")
public interface CategoryClient {

    @GetMapping("/categories/{id}/validate")
    boolean isCategoryValid(@PathVariable Long id);
}

