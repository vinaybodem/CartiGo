package com.cartigo.inventoryservice.service;

import com.cartigo.inventoryservice.entity.Inventory;
import jakarta.servlet.http.HttpServletRequest;

public interface InventoryService {

    Inventory createOrUpdate(Long productId, Integer totalStock, HttpServletRequest request);

    Inventory getByProductId(Long productId);
    Boolean checkAvailability(Long productId,Integer quantity);
    void reserveStock(Long productId, Integer quantity);

    void releaseStock(Long productId, Integer quantity);

    void reduceStock(Long productId, Integer quantity);
}
