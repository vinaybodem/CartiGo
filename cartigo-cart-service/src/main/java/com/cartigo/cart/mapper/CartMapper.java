package com.cartigo.cart.mapper;

import com.cartigo.cart.dto.CartItemResponse;
import com.cartigo.cart.entity.CartItem;

import java.math.BigDecimal;

public class CartMapper {

    private CartMapper() {}

    public static CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse r = new CartItemResponse();
        r.setId(item.getId());
        r.setProductId(item.getProductId());
        r.setQuantity(item.getQuantity());
        r.setProductName(item.getProductName());
        r.setImageUrl(item.getImageUrl());
        r.setUnitPrice(item.getUnitPrice());

        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        r.setLineTotal(lineTotal);
        return r;
    }
}
