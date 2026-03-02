package com.cartigo.product.mapper;

import com.cartigo.product.dto.ProductResponse;
import com.cartigo.product.entity.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setDiscountPrice(p.getDiscountPrice());
        r.setBrand(p.getBrand());
        r.setSku(p.getSku());
        r.setImage(p.getImage());
        r.setStatus(p.getStatus());
        r.setCategoryId(p.getCategoryId());
        r.setSellerId(p.getSellerId());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
