package com.cartigo.inventory.dto;

import jakarta.validation.constraints.NotNull;

public class AdjustInventoryRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Integer delta; // + increase, - decrease

    public AdjustInventoryRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getDelta() { return delta; }
    public void setDelta(Integer delta) { this.delta = delta; }
}
