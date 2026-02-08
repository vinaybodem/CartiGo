package com.cartigo.auditloggingservice.controller;

import com.cartigo.auditloggingservice.entity.AuditLog;
import com.cartigo.auditloggingservice.repository.AuditLogRepository;
import com.cartigo.common.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditLogRepository repo;
    private final ObjectMapper mapper;

    public AuditController(AuditLogRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @PostMapping
    public ApiResponse<AuditLog> log(@RequestBody Map<String, Object> payload) {
        AuditLog a = new AuditLog();
        a.setAction(String.valueOf(payload.getOrDefault("action", "UNKNOWN")));

        Object actor = payload.getOrDefault("actorId", 0);
        try {
            a.setActorId(Long.parseLong(String.valueOf(actor)));
        } catch (Exception e) {
            a.setActorId(0L);
        }

        Object data = payload.getOrDefault("data", payload);
        try {
            a.setDataJson(mapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            a.setDataJson("{}");
        }

        return ApiResponse.ok("Logged", repo.save(a));
    }

    @GetMapping
    public ApiResponse<List<AuditLog>> list() {
        return ApiResponse.ok("Audit logs", repo.findAll());
    }
}
