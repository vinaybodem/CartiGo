package com.cartigo.user.controller;

import com.cartigo.user.dto.AdminCreateRequest;
import com.cartigo.user.entity.Admin;
import com.cartigo.user.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Admin> create(@PathVariable Long userId, @Valid @RequestBody AdminCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdminProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Admin> get(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getAdmin(userId));
    }
}
