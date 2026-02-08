package com.cartigo.adminservice.controller;

import com.cartigo.common.dto.ApiResponse;
import com.cartigo.adminservice.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/products")
    public ApiResponse<List<Map<String, Object>>> listProducts(@RequestParam(required = false) String q,
                                                              @RequestParam(required = false) Long sellerId) {
        return ApiResponse.ok("Products", service.listProducts(q, sellerId));
    }

    @PostMapping("/products")
    public ApiResponse<Map<String, Object>> createProduct(@RequestParam Long adminId,
                                                         @RequestBody Map<String, Object> product) {
        return ApiResponse.ok("Created", service.createProduct(product, adminId));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Map<String, Object>> updateProduct(@PathVariable Long id,
                                                         @RequestParam Long adminId,
                                                         @RequestBody Map<String, Object> product) {
        return ApiResponse.ok("Updated", service.updateProduct(id, product, adminId));
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Object> deleteProduct(@PathVariable Long id, @RequestParam Long adminId) {
        service.deleteProduct(id, adminId);
        return ApiResponse.ok("Deleted", null);
    }

    @GetMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> getUser(@PathVariable Long id) {
        return ApiResponse.ok("User", service.getUser(id));
    }

    @PostMapping("/returns/{id}/approve")
    public ApiResponse<Map<String, Object>> approveReturn(@PathVariable Long id, @RequestParam Long adminId) {
        return ApiResponse.ok("Return approved", service.approveReturn(id, adminId));
    }

    @PostMapping("/returns/{id}/reject")
    public ApiResponse<Map<String, Object>> rejectReturn(@PathVariable Long id, @RequestParam Long adminId) {
        return ApiResponse.ok("Return rejected", service.rejectReturn(id, adminId));
    }
}
