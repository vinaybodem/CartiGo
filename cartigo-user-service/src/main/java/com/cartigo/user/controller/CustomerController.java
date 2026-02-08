package com.cartigo.user.controller;

import com.cartigo.user.dto.CustomerCreateRequest;
import com.cartigo.user.entity.Customer;
import com.cartigo.user.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Customer> create(@PathVariable Long userId, @Valid @RequestBody CustomerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomerProfile(userId, req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Customer> get(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomer(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Customer> update(@PathVariable Long userId, @Valid @RequestBody CustomerCreateRequest req) {
        return ResponseEntity.ok(customerService.updateCustomer(userId, req));
    }
}
