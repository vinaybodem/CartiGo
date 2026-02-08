package com.cartigo.adminservice.client;

import com.cartigo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "${user.service.name:cartigo-user-service}")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    ApiResponse<Map<String, Object>> get(@PathVariable Long id);
}
