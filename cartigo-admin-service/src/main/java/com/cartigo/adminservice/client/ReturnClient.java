package com.cartigo.adminservice.client;

import com.cartigo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "${returns.service.name:cartigo-return-refund-service}")
public interface ReturnClient {

    @GetMapping("/api/returns/{id}")
    ApiResponse<Map<String, Object>> get(@PathVariable Long id);

    @PostMapping("/api/returns/{id}/approve")
    ApiResponse<Map<String, Object>> approve(@PathVariable Long id, @RequestBody Map<String, Object> body);

    @PostMapping("/api/returns/{id}/reject")
    ApiResponse<Map<String, Object>> reject(@PathVariable Long id, @RequestBody Map<String, Object> body);
}
