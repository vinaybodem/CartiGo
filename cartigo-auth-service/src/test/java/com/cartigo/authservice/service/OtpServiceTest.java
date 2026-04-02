package com.cartigo.authservice.service;

import com.cartigo.authservice.entity.OtpEntity;
import com.cartigo.authservice.entity.OtpPurpose;
import com.cartigo.authservice.repository.OtpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OtpService otpService;

    // ✅ 1. GENERATE OTP (REGISTER)
    @Test
    void generateAndSaveOtpForRegister_shouldSaveOtp() {

        when(passwordEncoder.encode(any())).thenReturn("hashed");

        String otp = otpService.generateAndSaveOtpForRegister(
                "test@mail.com", "Suhel", "Basha", "pass", null
        );

        assertNotNull(otp);
        verify(otpRepository).deleteByEmailAndPurpose("test@mail.com", OtpPurpose.REGISTER);
        verify(otpRepository).save(any());
    }

    // ✅ 2. GENERATE OTP (LOGIN)
    @Test
    void generateAndSaveOtpForLogin_shouldSaveOtp() {

        when(passwordEncoder.encode(any())).thenReturn("hashed");

        String otp = otpService.generateAndSaveOtpForLogin("test@mail.com");

        assertNotNull(otp);
        verify(otpRepository).deleteByEmailAndPurpose("test@mail.com", OtpPurpose.LOGIN);
        verify(otpRepository).save(any());
    }

    // ❌ 3. VERIFY OTP NOT FOUND
    @Test
    void verifyOtp_shouldReturnEmpty_whenNoRecord() {

        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc(any(), any()))
                .thenReturn(Optional.empty());

        Optional<OtpEntity> result =
                otpService.verifyOtp("test@mail.com", "123456", OtpPurpose.LOGIN);

        assertTrue(result.isEmpty());
    }

    // ❌ 4. EXPIRED OTP
    @Test
    void verifyOtp_shouldReturnEmpty_whenExpired() {

        OtpEntity entity = new OtpEntity();
        entity.setExpiryTime(LocalDateTime.now().minusMinutes(1));

        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc(any(), any()))
                .thenReturn(Optional.of(entity));

        Optional<OtpEntity> result =
                otpService.verifyOtp("test@mail.com", "123456", OtpPurpose.LOGIN);

        assertTrue(result.isEmpty());
        verify(otpRepository).delete(entity);
    }

    // ❌ 5. WRONG OTP (INCREMENT ATTEMPT)
    @Test
    void verifyOtp_shouldIncrementAttempts_whenWrongOtp() {

        OtpEntity entity = new OtpEntity();
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        entity.setAttemptCount(0);
        entity.setOtpHash("hashed");

        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc(any(), any()))
                .thenReturn(Optional.of(entity));

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        Optional<OtpEntity> result =
                otpService.verifyOtp("test@mail.com", "wrong", OtpPurpose.LOGIN);

        assertTrue(result.isEmpty());
        assertEquals(1, entity.getAttemptCount());
        verify(otpRepository).save(entity);
    }

    // ❌ 6. MAX ATTEMPTS REACHED
    @Test
    void verifyOtp_shouldDelete_whenMaxAttemptsReached() {

        OtpEntity entity = new OtpEntity();
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        entity.setAttemptCount(5); // max reached

        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc(any(), any()))
                .thenReturn(Optional.of(entity));

        Optional<OtpEntity> result =
                otpService.verifyOtp("test@mail.com", "wrong", OtpPurpose.LOGIN);

        assertTrue(result.isEmpty());
        verify(otpRepository).delete(entity);
    }

    // ✅ 7. SUCCESS OTP
    @Test
    void verifyOtp_shouldReturnEntity_whenValidOtp() {

        OtpEntity entity = new OtpEntity();
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        entity.setAttemptCount(0);
        entity.setOtpHash("hashed");

        when(otpRepository.findTopByEmailAndPurposeOrderByIdDesc(any(), any()))
                .thenReturn(Optional.of(entity));

        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        Optional<OtpEntity> result =
                otpService.verifyOtp("test@mail.com", "123456", OtpPurpose.LOGIN);

        assertTrue(result.isPresent());
        verify(otpRepository).delete(entity);
    }
}