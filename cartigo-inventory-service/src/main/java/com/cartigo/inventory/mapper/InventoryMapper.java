package com.cartigo.inventory.mapper;

import com.cartigo.inventory.dto.InventoryResponse;
import com.cartigo.inventory.entity.InventoryItem;

public class InventoryMapper {

    private InventoryMapper() {}

    public static InventoryResponse toResponse(InventoryItem i) {
        InventoryResponse r = new InventoryResponse();
        r.setProductId(i.getProductId());
        r.setAvailableQty(i.getAvailableQty());
        r.setReservedQty(i.getReservedQty());
        r.setCreatedAt(i.getCreatedAt());
        r.setUpdatedAt(i.getUpdatedAt());
        return r;
    }
}
