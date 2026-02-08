package com.cartigo.cart.service;

import com.cartigo.cart.dto.AddToCartRequest;
import com.cartigo.cart.dto.CartItemResponse;
import com.cartigo.cart.dto.CartResponse;
import com.cartigo.cart.dto.UpdateCartItemRequest;
import com.cartigo.cart.entity.Cart;
import com.cartigo.cart.entity.CartItem;
import com.cartigo.cart.exception.BadRequestException;
import com.cartigo.cart.exception.ResourceNotFoundException;
import com.cartigo.cart.mapper.CartMapper;
import com.cartigo.cart.repository.CartItemRepository;
import com.cartigo.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return cartRepository.save(c);
        });

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return buildResponse(userId, items);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest req) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be > 0");
        }

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return cartRepository.save(c);
        });

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), req.getProductId())
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCartId(cart.getId());
                    ci.setProductId(req.getProductId());
                    ci.setQuantity(0);
                    ci.setProductName(req.getProductName());
                    ci.setImageUrl(req.getImageUrl());
                    ci.setUnitPrice(req.getUnitPrice());
                    return ci;
                });

        // Snapshot update (good)
        item.setProductName(req.getProductName());
        item.setImageUrl(req.getImageUrl());
        item.setUnitPrice(req.getUnitPrice());

        int currentQty = item.getQuantity() == null ? 0 : item.getQuantity();
        item.setQuantity(currentQty + req.getQuantity());

        cartItemRepository.save(item);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return buildResponse(userId, items);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long productId, UpdateCartItemRequest req) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId " + userId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for productId " + productId));

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return buildResponse(userId, items);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId " + userId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for productId " + productId));

        cartItemRepository.delete(item);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return buildResponse(userId, items);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId " + userId));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private CartResponse buildResponse(Long userId, List<CartItem> items) {
        List<CartItemResponse> out = items.stream()
                .map(CartMapper::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = out.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse r = new CartResponse();
        r.setUserId(userId);
        r.setItems(out);
        r.setTotalAmount(total);
        return r;
    }
}
