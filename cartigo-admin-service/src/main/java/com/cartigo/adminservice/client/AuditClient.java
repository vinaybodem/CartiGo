package com.cartigo.adminservice.client;

import com.cartigo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "${audit.service.name:cartigo-audit-logging-service}")
public interface AuditClient {

    @PostMapping("/audit")
    ApiResponse<Object> log(@RequestBody Map<String, Object> payload);
}
