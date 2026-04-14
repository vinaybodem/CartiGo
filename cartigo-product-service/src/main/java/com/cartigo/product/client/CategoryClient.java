package com.cartigo.product.client;

import com.cartigo.product.client.fallback.CategoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "CARTIGO-CATEGORY-SERVICE", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("api/category/validate/{category_id}")
    boolean isCategoryValid(@PathVariable Long category_id);

    @GetMapping("api/category/getId/{category_name}")
    public Long getCategroyId(@PathVariable String category_name);
}

