package com.cartigo.user.service;

import com.cartigo.user.dto.AdminCreateRequest;
import com.cartigo.user.entity.Admin;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    // ✅ 1. CREATE SUCCESS
    @Test
    void createAdminProfile_shouldCreateSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);

        AdminCreateRequest req = new AdminCreateRequest();
        req.setAdminName("Super Admin");

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.ADMIN);
        when(adminRepository.existsById(userId)).thenReturn(false);

        Admin saved = new Admin();
        saved.setId(userId);

        when(adminRepository.save(any())).thenReturn(saved);

        Admin result = adminService.createAdminProfile(userId, req);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    // ❌ 2. PROFILE EXISTS
    @Test
    void createAdminProfile_shouldThrow_whenAlreadyExists() {
        Long userId = 1L;

        User user = new User();
        user.setRole(Role.ADMIN);

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.ADMIN);
        when(adminRepository.existsById(userId)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> adminService.createAdminProfile(userId, new AdminCreateRequest()));
    }

    // ❌ 3. NOT FOUND
    @Test
    void getAdmin_shouldThrow_whenNotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminService.getAdmin(1L));
    }

    // ✅ 4. GET SUCCESS
    @Test
    void getAdmin_shouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setId(1L);

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdmin(1L);

        assertEquals(1L, result.getId());
    }
}