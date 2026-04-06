package com.cartigo.cart.service;

import com.cartigo.cart.client.InventoryClient;
import com.cartigo.cart.client.NotificationClient;
import com.cartigo.cart.client.ProductClient;
import com.cartigo.cart.dto.*;
import com.cartigo.cart.entity.Cart;
import com.cartigo.cart.entity.CartItem;
import com.cartigo.cart.exception.BadRequestException;
import com.cartigo.cart.exception.ResourceNotFoundException;
import com.cartigo.cart.repository.CartItemRepository;
import com.cartigo.cart.repository.CartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private CartService cartService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        AuthPrinciple principal = new AuthPrinciple(1L, "user@example.com", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getOrCreateCart_whenCartExists_returnsCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getOrCreateCart_whenCartDoesNotExist_createsAndReturnsCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setUserId(1L);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Cart result = cartService.getOrCreateCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItem_whenQuantityNull_throwsBadRequestException() {
        AddToCartRequest req = new AddToCartRequest();
        req.setQuantity(null);
        assertThrows(BadRequestException.class, () -> cartService.addItem(1L, req));
    }

    @Test
    void addItem_whenQuantityZero_throwsBadRequestException() {
        AddToCartRequest req = new AddToCartRequest();
        req.setQuantity(0);
        assertThrows(BadRequestException.class, () -> cartService.addItem(1L, req));
    }

    @Test
    void addItem_whenProductNotFound_throwsResourceNotFoundException() {
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(1L);
        req.setQuantity(1);

        Cart cart = new Cart();
        cart.setId(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productClient.getProduct(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(1L, req));
    }

    @Test
    void addItem_whenInventoryNotAvailable_throwsBadRequestException() {
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(1L);
        req.setQuantity(1);

        Cart cart = new Cart();
        cart.setId(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        
        ProductResponse product = new ProductResponse();
        product.setId(1L);
        when(productClient.getProduct(1L)).thenReturn(product);
        when(inventoryClient.checkAvailability(1L, 1)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> cartService.addItem(1L, req));
    }

    @Test
    void addItem_successful() {
        setupSecurityContext();

        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(100L);
        req.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        ProductResponse product = new ProductResponse();
        product.setId(100L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(10.0));
        when(productClient.getProduct(100L)).thenReturn(product);

        when(inventoryClient.checkAvailability(100L, 2)).thenReturn(true);

        CartItem existingItem = new CartItem();
        existingItem.setCartId(10L);
        existingItem.setProductId(100L);
        existingItem.setQuantity(1);
        existingItem.setUnitPrice(BigDecimal.TEN);
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.of(existingItem));

        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(existingItem));

        CartResponse response = cartService.addItem(1L, req);

        assertEquals(3, existingItem.getQuantity());
        assertEquals("Test Product", existingItem.getProductName());
        verify(cartItemRepository).save(existingItem);
        verify(notificationClient).sendEmail(any(NotificationRequest.class));
        assertNotNull(response);
    }

    @Test
    void updateItem_whenCartNotFound_throwsResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateItem(1L, 100L, new UpdateCartItemRequest()));
    }

    @Test
    void updateItem_whenItemNotFound_throwsResourceNotFoundException() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateItem(1L, 100L, new UpdateCartItemRequest()));
    }

    @Test
    void updateItem_whenQuantityZero_deletesItem() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.TEN);
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.of(item));

        UpdateCartItemRequest req = new UpdateCartItemRequest();
        req.setQuantity(0);

        when(cartItemRepository.findByCartId(10L)).thenReturn(Collections.emptyList());

        CartResponse response = cartService.updateItem(1L, 100L, req);

        verify(cartItemRepository).delete(item);
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void updateItem_whenInventoryNotAvailable_throwsBadRequestException() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.TEN);
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.of(item));

        UpdateCartItemRequest req = new UpdateCartItemRequest();
        req.setQuantity(5);

        when(inventoryClient.checkAvailability(100L, 5)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> cartService.updateItem(1L, 100L, req));
    }

    @Test
    void updateItem_successful() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setUnitPrice(BigDecimal.TEN);
        item.setQuantity(1);
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.of(item));

        UpdateCartItemRequest req = new UpdateCartItemRequest();
        req.setQuantity(5);

        when(inventoryClient.checkAvailability(100L, 5)).thenReturn(true);
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(item));

        CartResponse response = cartService.updateItem(1L, 100L, req);

        verify(cartItemRepository).save(item);
        assertEquals(5, item.getQuantity());
        assertNotNull(response);
    }

    @Test
    void removeItem_successful() {
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUserId(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.TEN);
        when(cartItemRepository.findByCartIdAndProductId(10L, 100L)).thenReturn(Optional.of(item));

        when(cartItemRepository.findByCartId(10L)).thenReturn(Collections.emptyList());

        CartResponse response = cartService.removeItem(1L, 100L);

        verify(cartItemRepository).delete(item);
        assertNotNull(response);
    }

    @Test
    void clearCart_successful() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        verify(cartItemRepository).deleteByCartId(10L);
    }

    @Test
    void getCart_successful() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setUnitPrice(BigDecimal.TEN);
        item.setQuantity(2);
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(item));

        CartResponse response = cartService.getCart(1L);

        assertNotNull(response);
        assertEquals(10L, response.getCartId());
        assertEquals(1, response.getItems().size());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(response.getTotalAmount()));
    }

    @Test
    void validateCheckout_whenCartEmpty_throwsBadRequestException() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(10L)).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> cartService.validateCheckout(1L));
    }

    @Test
    void validateCheckout_whenItemOutOfStock_throwsBadRequestException() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.TEN);
        item.setProductName("Test Item");
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(item));

        when(inventoryClient.checkAvailability(100L, 2)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> cartService.validateCheckout(1L));
    }

    @Test
    void validateCheckout_successful() {
        Cart cart = new Cart();
        cart.setId(10L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartItem item = new CartItem();
        item.setCartId(10L);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.TEN);
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(item));

        when(inventoryClient.checkAvailability(100L, 2)).thenReturn(true);

        CheckoutValidationResponse response = cartService.validateCheckout(1L);

        assertTrue(response.isValid());
    }
}
