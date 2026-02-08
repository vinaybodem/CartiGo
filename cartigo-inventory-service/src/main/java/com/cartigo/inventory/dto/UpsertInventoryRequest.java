package com.cartigo.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class UpsertInventoryRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Integer availableQty;

    public UpsertInventoryRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getAvailableQty() { return availableQty; }
    public void setAvailableQty(Integer availableQty) { this.availableQty = availableQty; }
}
