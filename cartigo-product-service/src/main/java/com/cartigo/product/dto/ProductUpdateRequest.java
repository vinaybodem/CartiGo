package com.cartigo.product.dto;

import com.cartigo.product.entity.ProductStatus;

import java.math.BigDecimal;

public class ProductUpdateRequest {

    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private ProductStatus isActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public ProductStatus getStatus() {
        return isActive;
    }

    public void setStatus(ProductStatus isActive) {
        this.isActive = isActive;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }


}
