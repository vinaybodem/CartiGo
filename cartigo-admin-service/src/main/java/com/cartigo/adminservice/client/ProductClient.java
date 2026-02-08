package com.cartigo.adminservice.client;

import com.cartigo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${product.service.name:cartigo-product-service}")
public interface ProductClient {

    @GetMapping("/api/products")
    ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) Long sellerId);

    @PostMapping("/api/products")
    ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> p);

    @PutMapping("/api/products/{id}")
    ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> p);

    @DeleteMapping("/api/products/{id}")
    ApiResponse<Object> delete(@PathVariable Long id);
}
