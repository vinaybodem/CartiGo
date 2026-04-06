package com.cartigo.inventoryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer totalStock = 0;
    private Integer reservedStock = 0;
    private Integer availableStock = 0;

    private Boolean isActive = true;

    public Inventory() {
    }

    public Inventory(Long productId, Integer reservedStock) {
        this.productId = productId;
        this.reservedStock = reservedStock;
    }

    public Inventory(Long id, Long productId, Integer totalStock, Integer reservedStock, Integer availableStock, Boolean isActive) {
        this.id = id;
        this.productId = productId;
        this.totalStock = totalStock;
        this.reservedStock = reservedStock;
        this.availableStock = availableStock;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
