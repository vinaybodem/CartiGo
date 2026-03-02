package com.cartigo.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "CARTIGO-CATEGORY-SERVICE")
public interface CategoryClient {

    @GetMapping("/categories/{id}/validate")
    boolean isCategoryValid(@PathVariable Long id);

    @GetMapping("api/category/getId/{category_name}")
    public Long getCategroyId(@PathVariable String category_name);
}

