package com.cartigo.product.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductImageRequest {

    @NotBlank
    private String imageUrl;

    private Boolean isPrimary;

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}