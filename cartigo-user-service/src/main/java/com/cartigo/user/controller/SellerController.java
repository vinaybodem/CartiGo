package com.cartigo.user.controller;

import com.cartigo.user.dto.SellerCreateRequest;
import com.cartigo.user.entity.Seller;
import com.cartigo.user.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Seller> create(@PathVariable Long userId, @Valid @RequestBody SellerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerService.createSellerProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Seller> get(@PathVariable Long userId) {
        return ResponseEntity.ok(sellerService.getSeller(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Seller> update(@PathVariable Long userId, @Valid @RequestBody SellerCreateRequest req) {
        return ResponseEntity.ok(sellerService.updateSeller(userId, req));
    }

    // Temporary endpoint for demo; in real app admin-service should call internally.
    @PatchMapping("/{userId}/approved")
    public ResponseEntity<Seller> approve(@PathVariable Long userId, @RequestParam boolean value) {
        return ResponseEntity.ok(sellerService.setApproved(userId, value));
    }
}
