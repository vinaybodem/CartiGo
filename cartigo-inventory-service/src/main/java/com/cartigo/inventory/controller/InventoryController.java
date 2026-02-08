package com.cartigo.inventory.controller;

import com.cartigo.inventory.dto.*;
import com.cartigo.inventory.mapper.InventoryMapper;
import com.cartigo.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // ADMIN/SELLER: create or set stock
    @PostMapping
    public ResponseEntity<InventoryResponse> upsert(@Valid @RequestBody UpsertInventoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryMapper.toResponse(service.upsert(req)));
    }

    // ADMIN/SELLER: adjust stock by delta
    @PatchMapping("/adjust")
    public ResponseEntity<InventoryResponse> adjust(@Valid @RequestBody AdjustInventoryRequest req) {
        return ResponseEntity.ok(InventoryMapper.toResponse(service.adjust(req)));
    }

    // CHECKOUT: reserve stock
    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserve(@Valid @RequestBody ReserveRequest req) {
        return ResponseEntity.ok(InventoryMapper.toResponse(service.reserve(req)));
    }

    // CHECKOUT: release stock
    @PostMapping("/release")
    public ResponseEntity<InventoryResponse> release(@Valid @RequestBody ReserveRequest req) {
        return ResponseEntity.ok(InventoryMapper.toResponse(service.release(req)));
    }

    // ORDER: confirm stock (payment success)
    @PostMapping("/confirm")
    public ResponseEntity<InventoryResponse> confirm(@Valid @RequestBody ReserveRequest req) {
        return ResponseEntity.ok(InventoryMapper.toResponse(service.confirm(req)));
    }

    // Read inventory
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> get(@PathVariable Long productId) {
        return ResponseEntity.ok(InventoryMapper.toResponse(service.get(productId)));
    }

    // Simple stock check (for product-service/cart-service)
    @GetMapping("/{productId}/in-stock")
    public ResponseEntity<Boolean> inStock(@PathVariable Long productId, @RequestParam(defaultValue = "1") int qty) {
        return ResponseEntity.ok(service.inStock(productId, qty));
    }
}
