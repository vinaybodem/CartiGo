package com.cartigo.inventoryservice.dao;

public class ProductResponse {

    private Long id;
    private Long sellerId;

    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", sellerId=" + sellerId +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}
