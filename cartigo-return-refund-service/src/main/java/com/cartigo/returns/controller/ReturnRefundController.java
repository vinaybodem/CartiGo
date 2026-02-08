package com.cartigo.returns.controller;

import com.cartigo.returns.dto.*;
import com.cartigo.returns.mapper.RefundMapper;
import com.cartigo.returns.mapper.ReturnMapper;
import com.cartigo.returns.service.ReturnRefundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/returns")
public class ReturnRefundController {

    private final ReturnRefundService service;

    public ReturnRefundController(ReturnRefundService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReturnResponse> create(@Valid @RequestBody ReturnCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnMapper.toResponse(service.createReturn(req)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReturnResponse>> listByUser(@PathVariable Long userId) {
        List<ReturnResponse> out = service.listByUser(userId).stream()
                .map(ReturnMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping
    public ResponseEntity<List<ReturnResponse>> listAll() {
        List<ReturnResponse> out = service.listAll().stream()
                .map(ReturnMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(ReturnMapper.toResponse(service.getReturn(id)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ReturnResponse> approve(@PathVariable Long id, @Valid @RequestBody ReturnDecisionRequest req) {
        return ResponseEntity.ok(ReturnMapper.toResponse(service.approve(id, req)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ReturnResponse> reject(@PathVariable Long id, @Valid @RequestBody ReturnDecisionRequest req) {
        return ResponseEntity.ok(ReturnMapper.toResponse(service.reject(id, req)));
    }

    @GetMapping("/{id}/refund")
    public ResponseEntity<RefundResponse> getRefund(@PathVariable Long id) {
        return ResponseEntity.ok(RefundMapper.toResponse(service.getRefundByReturnId(id)));
    }

    @PostMapping("/{id}/refund/success")
    public ResponseEntity<RefundResponse> refundSuccess(@PathVariable Long id,
                                                        @RequestParam String gatewayRefundId) {
        return ResponseEntity.ok(RefundMapper.toResponse(service.markRefundSuccess(id, gatewayRefundId)));
    }

    @PostMapping("/{id}/refund/failed")
    public ResponseEntity<RefundResponse> refundFailed(@PathVariable Long id,
                                                       @RequestParam String gatewayRefundId) {
        return ResponseEntity.ok(RefundMapper.toResponse(service.markRefundFailed(id, gatewayRefundId)));
    }
}
