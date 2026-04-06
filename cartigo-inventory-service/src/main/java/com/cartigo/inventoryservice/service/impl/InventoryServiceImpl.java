package com.cartigo.inventoryservice.service.impl;

import com.cartigo.inventoryservice.client.ProductClient;
import com.cartigo.inventoryservice.config.JwtUtil;
import com.cartigo.inventoryservice.dao.ProductResponse;
import com.cartigo.inventoryservice.entity.Inventory;
import com.cartigo.inventoryservice.exception.ResourceNotFoundException;
import com.cartigo.inventoryservice.repository.InventoryRepository;
import com.cartigo.inventoryservice.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final JwtUtil jwtUtil;
    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;
    public InventoryServiceImpl(JwtUtil jwtUtil, InventoryRepository inventoryRepository, ProductClient productClient) {
        this.jwtUtil = jwtUtil;
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
    }

    @Override
    public Inventory createOrUpdate(Long productId, Integer totalStock,HttpServletRequest request) {

        ProductResponse product = productClient.validate(productId);
        System.out.println(product);
        if (product.getSellerId() == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        Long sellerId = getAuthenticatedSellerId(request);

        System.out.println("SellerId: " + sellerId + "productId:"+product.getSellerId());
        System.out.println(product.getId());
        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Not allowed");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(new Inventory(productId,0) );

        Integer currentStock = inventory.getTotalStock() == null ? 0 : inventory.getTotalStock();
        inventory.setTotalStock(currentStock + totalStock);
        inventory.setAvailableStock(
                 inventory.getTotalStock()
        );

        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory getByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
    }

    @Override
    public Boolean checkAvailability(Long productId,Integer quantity) {
        Inventory inventory = getByProductId(productId);
        if(inventory.getAvailableStock()<=0 && inventory.getAvailableStock()>quantity) return false;
        return true;
    }

    @Override
    public void reserveStock(Long productId, Integer quantity) {

        ProductResponse product = productClient.validate(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        Inventory inventory = getByProductId(productId);

        int available = inventory.getAvailableStock() == null ? 0 : inventory.getAvailableStock();
        int reserved = inventory.getReservedStock() == null ? 0 : inventory.getReservedStock();

        if (available < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setReservedStock(reserved + quantity);
        inventory.setAvailableStock(available - quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    public void releaseStock(Long productId, Integer quantity) {

        ProductResponse product = productClient.validate(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        Inventory inventory = getByProductId(productId);

        int reserved = inventory.getReservedStock() == null ? 0 : inventory.getReservedStock();
        int available = inventory.getAvailableStock() == null ? 0 : inventory.getAvailableStock();

        if (reserved < quantity) {
            throw new RuntimeException("Cannot release more than reserved stock");
        }

        inventory.setReservedStock(reserved - quantity);
        inventory.setAvailableStock(available + quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    public void reduceStock(Long productId, Integer quantity) {
        ProductResponse product = productClient.validate(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        Inventory inventory = getByProductId(productId);

        inventory.setTotalStock(inventory.getTotalStock() - quantity);
        inventory.setReservedStock(inventory.getReservedStock() - quantity);
        inventory.setAvailableStock(inventory.getAvailableStock());

        inventoryRepository.save(inventory);
    }

    private Long getAuthenticatedSellerId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthenticated request");
        }

        String token = authHeader.substring(7);
        Long sellerId = jwtUtil.extractUserId(token);

        if (sellerId == null) {
            throw new RuntimeException("Invalid token userId");
        }

        return sellerId;
    }
}
