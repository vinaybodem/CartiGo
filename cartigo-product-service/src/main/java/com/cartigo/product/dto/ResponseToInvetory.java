package com.cartigo.product.dto;

public class ResponseToInvetory {
    private Long id;
    private Long sellerId;

    public ResponseToInvetory(Long id, Long sellerId) {
        this.id = id;
        this.sellerId = sellerId;
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
