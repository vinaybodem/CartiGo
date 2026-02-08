package com.cartigo.inventory.service;

import com.cartigo.inventory.dto.AdjustInventoryRequest;
import com.cartigo.inventory.dto.ReserveRequest;
import com.cartigo.inventory.dto.UpsertInventoryRequest;
import com.cartigo.inventory.entity.InventoryItem;
import com.cartigo.inventory.exception.BadRequestException;
import com.cartigo.inventory.exception.ResourceNotFoundException;
import com.cartigo.inventory.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryItemRepository repo;

    public InventoryService(InventoryItemRepository repo) {
        this.repo = repo;
    }

    /**
     * Create or set stock for a product (ADMIN/SELLER).
     */
    @Transactional
    public InventoryItem upsert(UpsertInventoryRequest req) {
        InventoryItem item = repo.findById(req.getProductId())
                .orElseGet(InventoryItem::new);

        item.setProductId(req.getProductId());

        int newAvail = req.getAvailableQty();
        if (newAvail < 0) {
            throw new BadRequestException("availableQty cannot be negative");
        }

        int reserved = item.getReservedQty() == null ? 0 : item.getReservedQty();

        if (newAvail < reserved) {
            throw new BadRequestException(
                    "availableQty cannot be less than reservedQty (" + reserved + ")"
            );
        }

        item.setAvailableQty(newAvail);
        item.setReservedQty(reserved);

        return repo.save(item);
    }


    /**
     * Adjust available stock (ADMIN/SELLER). delta can be + or -.
     */
    @Transactional
    public InventoryItem adjust(AdjustInventoryRequest req) {
        InventoryItem item = repo.lockByProductId(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId " + req.getProductId()));

        int newAvail = item.getAvailableQty() + req.getDelta();
        if (newAvail < 0) throw new BadRequestException("Not enough stock to decrease");

        item.setAvailableQty(newAvail);
        return repo.save(item);
    }

    /**
     * Reserve stock when customer starts checkout.
     * available decreases, reserved increases.
     */
    @Transactional
    public InventoryItem reserve(ReserveRequest req) {
        InventoryItem item = repo.lockByProductId(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId " + req.getProductId()));

        if (req.getQuantity() <= 0) throw new BadRequestException("quantity must be > 0");

        if (item.getAvailableQty() < req.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

        item.setAvailableQty(item.getAvailableQty() - req.getQuantity());
        item.setReservedQty(item.getReservedQty() + req.getQuantity());
        return repo.save(item);
    }

    /**
     * Release reserved stock if payment failed/cancelled.
     * reserved decreases, available increases.
     */
    @Transactional
    public InventoryItem release(ReserveRequest req) {
        InventoryItem item = repo.lockByProductId(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId " + req.getProductId()));

        if (req.getQuantity() <= 0) throw new BadRequestException("quantity must be > 0");
        if (item.getReservedQty() < req.getQuantity()) throw new BadRequestException("reservedQty is less than release quantity");

        item.setReservedQty(item.getReservedQty() - req.getQuantity());
        item.setAvailableQty(item.getAvailableQty() + req.getQuantity());
        return repo.save(item);
    }

    /**
     * Confirm reserved stock after successful payment (order placed).
     * reserved decreases, available unchanged.
     */
    @Transactional
    public InventoryItem confirm(ReserveRequest req) {
        InventoryItem item = repo.lockByProductId(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId " + req.getProductId()));

        if (req.getQuantity() <= 0) throw new BadRequestException("quantity must be > 0");
        if (item.getReservedQty() < req.getQuantity()) throw new BadRequestException("reservedQty is less than confirm quantity");

        item.setReservedQty(item.getReservedQty() - req.getQuantity());
        return repo.save(item);
    }

    public InventoryItem get(Long productId) {
        return repo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId " + productId));
    }

    public boolean inStock(Long productId, int neededQty) {
        InventoryItem item = repo.lockByProductId(productId).orElse(null);
        return item != null && item.getAvailableQty() >= neededQty;
    }
}
