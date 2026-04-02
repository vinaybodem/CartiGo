package com.cartigo.user.service;

import com.cartigo.user.dto.UserCreateFromAuthRequest;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ✅ 1. SUCCESS CASE
    @Test
    void createUserFromAuth_shouldCreateUserSuccessfully() {
        UserCreateFromAuthRequest req = new UserCreateFromAuthRequest();
        req.setId(1L);
        req.setFirstName("Suhel");
        req.setLastName("Basha");
        req.setEmail("test@mail.com");
        req.setRole(Role.CUSTOMER);

        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        User saved = new User();
        saved.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.createUserFromAuth(req);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(any(User.class));
    }

    // ❌ 2. ID NULL
    @Test
    void createUserFromAuth_shouldThrow_whenIdNull() {
        UserCreateFromAuthRequest req = new UserCreateFromAuthRequest();

        assertThrows(BadRequestException.class,
                () -> userService.createUserFromAuth(req));
    }

    // ❌ 3. EMAIL EXISTS
    @Test
    void createUserFromAuth_shouldThrow_whenEmailExists() {
        UserCreateFromAuthRequest req = new UserCreateFromAuthRequest();
        req.setId(1L);
        req.setEmail("test@mail.com");

        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> userService.createUserFromAuth(req));
    }

    // 🔁 4. IDEMPOTENT CASE
    @Test
    void createUserFromAuth_shouldReturnExistingUser_whenIdExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.createUserFromAuth(new UserCreateFromAuthRequest() {{
            setId(1L);
        }});

        assertEquals(1L, result.getId());
    }

    // ❌ 5. USER NOT FOUND
    @Test
    void getUser_shouldThrow_whenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUser(1L));
    }

    // ✅ 6. GET USER SUCCESS
    @Test
    void getUser_shouldReturnUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals(1L, result.getId());
    }

    // ✅ 7. GET ALL USERS
    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    // ✅ 8. SET ACTIVE
    @Test
    void setActive_shouldUpdateStatus() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.setActive(1L, false);

        assertFalse(result.getIsActive());
    }

    // ✅ 9. DELETE USER
    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    // ❌ 10. ROLE VALIDATION
    @Test
    void assertRole_shouldThrow_whenInvalidRole() {
        User user = new User();
        user.setRole(Role.CUSTOMER);

        assertThrows(BadRequestException.class,
                () -> userService.assertRole(user, Role.ADMIN));
    }
}