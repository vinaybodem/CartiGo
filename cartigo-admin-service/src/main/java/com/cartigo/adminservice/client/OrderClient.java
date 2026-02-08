package com.cartigo.adminservice.client;

import com.cartigo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${order.service.name:cartigo-order-service}")
public interface OrderClient {

    @GetMapping("/api/orders/user/{userId}")
    ApiResponse<List<Map<String, Object>>> listByUser(@PathVariable Long userId);
}
