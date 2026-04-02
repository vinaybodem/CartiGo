package com.cartigo.user.service;

import com.cartigo.user.dto.SellerCreateRequest;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.Seller;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SellerService sellerService;

    // ✅ 1. SUCCESS CREATE
    @Test
    void createSellerProfile_shouldCreateSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setRole(Role.SELLER);

        SellerCreateRequest req = new SellerCreateRequest();
        req.setStoreName("My Store");
        req.setGstNumber("GST123");
        req.setBusinessAddress("Address");

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.SELLER);
        when(sellerRepository.existsById(userId)).thenReturn(false);
        when(sellerRepository.existsByGstNumber("GST123")).thenReturn(false);

        Seller saved = new Seller();
        saved.setId(userId);

        when(sellerRepository.save(any())).thenReturn(saved);

        Seller result = sellerService.createSellerProfile(userId, req);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    // ❌ 2. PROFILE ALREADY EXISTS
    @Test
    void createSellerProfile_shouldThrow_whenAlreadyExists() {
        Long userId = 1L;

        User user = new User();
        user.setRole(Role.SELLER);

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.SELLER);
        when(sellerRepository.existsById(userId)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> sellerService.createSellerProfile(userId, new SellerCreateRequest()));
    }

    // ❌ 3. GST DUPLICATE
    @Test
    void createSellerProfile_shouldThrow_whenGstExists() {
        Long userId = 1L;

        User user = new User();
        user.setRole(Role.SELLER);

        SellerCreateRequest req = new SellerCreateRequest();
        req.setGstNumber("GST123");

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.SELLER);
        when(sellerRepository.existsById(userId)).thenReturn(false);
        when(sellerRepository.existsByGstNumber("GST123")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> sellerService.createSellerProfile(userId, req));
    }

    // ❌ 4. NOT FOUND
    @Test
    void getSeller_shouldThrow_whenNotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sellerService.getSeller(1L));
    }

    // ✅ 5. GET SUCCESS
    @Test
    void getSeller_shouldReturnSeller() {
        Seller seller = new Seller();
        seller.setId(1L);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Seller result = sellerService.getSeller(1L);

        assertEquals(1L, result.getId());
    }

    // ✅ 6. UPDATE SELLER
    @Test
    void updateSeller_shouldUpdateFields() {
        Long userId = 1L;

        Seller seller = new Seller();
        seller.setId(userId);

        SellerCreateRequest req = new SellerCreateRequest();
        req.setStoreName("Updated Store");

        when(sellerRepository.findById(userId)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any())).thenReturn(seller);

        Seller result = sellerService.updateSeller(userId, req);

        assertEquals("Updated Store", result.getStoreName());
    }

    // ✅ 7. APPROVE SELLER
    @Test
    void setApproved_shouldUpdateStatus() {
        Seller seller = new Seller();
        seller.setId(1L);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any())).thenReturn(seller);

        Seller result = sellerService.setApproved(1L, true);

        assertTrue(result.getIsApproved());
    }
}