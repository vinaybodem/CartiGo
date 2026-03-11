package com.cartigo.inventoryservice.controller;

import com.cartigo.inventoryservice.common.ApiResponse;
import com.cartigo.inventoryservice.entity.Inventory;
import com.cartigo.inventoryservice.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping("/create/{productId}")
    public ApiResponse<Inventory> createOrUpdate(
            @PathVariable Long productId,
            @RequestParam Integer totalStock, HttpServletRequest request) {

        return ApiResponse.ok(
                   "Inventory updated",
                inventoryService.createOrUpdate(productId, totalStock,request)
        );
    }

    // 🔹 Used by Product / Cart services
    @GetMapping("/product/{productId}")
    public ApiResponse<Inventory> getByProduct(@PathVariable Long productId) {
        return ApiResponse.ok(
                "Inventory",
                inventoryService.getByProductId(productId)
        );
    }

    // 🔹 Used by Order Service
    @PostMapping("/{productId}/reserve")
    public ApiResponse<Object> reserve(
            @PathVariable Long productId,
            @RequestParam Integer qty) {

        inventoryService.reserveStock(productId, qty);
        return ApiResponse.ok("Reserved", null);
    }

    @PostMapping("/{productId}/release")
    public ApiResponse<Object> release(
            @PathVariable Long productId,
            @RequestParam Integer qty) {

        inventoryService.releaseStock(productId, qty);
        return ApiResponse.ok("Released", null);
    }

    @PostMapping("/{productId}/reduce")
    public ApiResponse<Object> reduce(
            @PathVariable Long productId,
            @RequestParam Integer qty) {

        inventoryService.reduceStock(productId, qty);
        return ApiResponse.ok("Reduced", null);
    }

    @GetMapping("/available/{productId}")
    Boolean checkAvailability(@PathVariable Long productId, @RequestParam Integer quantity){
       return inventoryService.checkAvailability(productId,quantity);
    }
}

