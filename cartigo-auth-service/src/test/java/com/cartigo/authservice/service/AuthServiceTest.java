package com.cartigo.authservice.service;

import com.cartigo.authservice.client.UserServiceClient;
import com.cartigo.authservice.dto.AuthResponse;
import com.cartigo.authservice.dto.LoginRequest;
import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.Role;
import com.cartigo.authservice.entity.User;
import com.cartigo.authservice.repository.UserRepository;
import com.cartigo.authservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    // ✅ 1. REGISTER SUCCESS
    @Test
    void registerUserAndCreateProfile_shouldCreateUserAndCallUserService() {

        ReflectionTestUtils.setField(authService, "userServiceEnabled", true);
        OtpEntity otp = new OtpEntity();
        otp.setEmail("test@mail.com");
        otp.setTempPasswordHash("hashed");
        otp.setTempRole(Role.CUSTOMER);
        otp.setTempFirstName("Suhel");
        otp.setTempLastName("Basha");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        User saved = new User();
        saved.setId(1L);
        saved.setEmail("test@mail.com");

        when(userRepository.save(any())).thenReturn(saved);

        authService.registerUserAndCreateProfile(otp);

        verify(userRepository).save(any());
        verify(userServiceClient).createUserFromAuth(any());
    }

    // 🔁 2. REGISTER IDEMPOTENT (USER EXISTS)
    @Test
    void registerUserAndCreateProfile_shouldSkip_whenUserExists() {

        OtpEntity otp = new OtpEntity();
        otp.setEmail("test@mail.com");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        authService.registerUserAndCreateProfile(otp);

        verify(userRepository, never()).save(any());
        verify(userServiceClient, never()).createUserFromAuth(any());
    }

    // ✅ 3. LOGIN SUCCESS
    @Test
    void loginWithPassword_shouldReturnToken() {

        LoginRequest req = new LoginRequest();
        req.setEmail("test@mail.com");
        req.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(any(), any(), any()))
                .thenReturn("mock-token");

        AuthResponse response = authService.loginWithPassword(req);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
    }

    // ❌ 4. LOGIN FAILED (NOT AUTHENTICATED)
    @Test
    void loginWithPassword_shouldThrow_whenNotAuthenticated() {

        LoginRequest req = new LoginRequest();
        req.setEmail("test@mail.com");
        req.setPassword("wrong");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);

        assertThrows(RuntimeException.class,
                () -> authService.loginWithPassword(req));
    }

    // ❌ 5. LOGIN USER NOT FOUND
    @Test
    void loginWithPassword_shouldThrow_whenUserNotFound() {

        LoginRequest req = new LoginRequest();
        req.setEmail("test@mail.com");
        req.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.loginWithPassword(req));
    }

    // ✅ 6. ISSUE TOKEN
    @Test
    void issueTokenForEmail_shouldReturnToken() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(any(), any(), any()))
                .thenReturn("token");

        AuthResponse response = authService.issueTokenForEmail("test@mail.com");

        assertEquals("token", response.getToken());
    }

    // ❌ 7. ISSUE TOKEN USER NOT FOUND
    @Test
    void issueTokenForEmail_shouldThrow_whenUserNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.issueTokenForEmail("test@mail.com"));
    }
}