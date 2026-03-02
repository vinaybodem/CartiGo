package com.cartigo.product.dto;

import com.cartigo.product.entity.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotBlank
    private String brand;
    @NotBlank
    private String sku;

    @NotBlank
    private ProductImage image;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long sellerId;

    public ProductCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public ProductImage getImage() { return image; }
    public void setImageUrl(ProductImage imageUrl) { this.image = image; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}
