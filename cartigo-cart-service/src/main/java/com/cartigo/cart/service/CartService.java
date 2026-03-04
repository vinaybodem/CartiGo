package com.cartigo.cart.service;

import com.cartigo.cart.client.InventoryClient;
import com.cartigo.cart.client.ProductClient;
import com.cartigo.cart.dto.*;
import com.cartigo.cart.entity.Cart;
import com.cartigo.cart.entity.CartItem;
import com.cartigo.cart.exception.BadRequestException;
import com.cartigo.cart.exception.ResourceNotFoundException;
import com.cartigo.cart.mapper.CartMapper;
import com.cartigo.cart.repository.CartItemRepository;
import com.cartigo.cart.repository.CartRepository;
//import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductClient productClient,
            InventoryClient inventoryClient
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest req) {

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(userId);
        System.out.println(cart);
        System.out.println("card id: "+cart.getId());
        // Always fetch product from product-service (do NOT trust request payload)
        ProductResponse product = productClient.getProduct(req.getProductId());

        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        // Check inventory availability
        Boolean available = inventoryClient.checkAvailability(req.getProductId());
        if (Boolean.FALSE.equals(available)) {
            throw new BadRequestException("Requested quantity not available in inventory");
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCartId(cart.getId());
                    ci.setProductId(product.getId());
                    ci.setQuantity(0);
                    return ci;
                });

        int updatedQty = (item.getQuantity() == null ? 0 : item.getQuantity()) + req.getQuantity();

        // Snapshot product details
        item.setProductName(product.getName());
        item.setImageUrl(product.getImageUrl());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(updatedQty);

        cartItemRepository.save(item);

        return buildCartResponse(userId, cart.getId());
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long productId, UpdateCartItemRequest req) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found for userId " + userId));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found for productId " + productId));

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {

            Boolean available = inventoryClient.checkAvailability(productId);

            if (Boolean.FALSE.equals(available)) {
                throw new BadRequestException("Requested quantity not available");
            }

            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        return buildCartResponse(userId, cart.getId());
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found for userId " + userId));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found for productId " + productId));

        cartItemRepository.delete(item);

        return buildCartResponse(userId, cart.getId());
    }

    @Transactional
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found for userId " + userId));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found for userId " + userId));

        return buildCartResponse(userId, cart.getId());
    }
    @Transactional(readOnly = true)
    public CheckoutValidationResponse validateCheckout(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        if (items.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for (CartItem item : items) {

            Boolean available = inventoryClient.checkAvailability(
                    item.getProductId()
            );

            if (Boolean.FALSE.equals(available)) {
                throw new BadRequestException(
                        "Product out of stock: " + item.getProductName()
                );
            }
        }

        return new CheckoutValidationResponse(true,"Cart validated successfully");
    }
    private CartResponse buildCartResponse(Long userId, Long cartId) {

        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        List<CartItemResponse> itemResponses = items.stream()
                .map(CartMapper::toItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse response = new CartResponse();
        response.setCartId(cartId);
        response.setUserId(userId);
        response.setItems(itemResponses);
        response.setTotalAmount(total);

        return response;
    }
}