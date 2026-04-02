package com.cartigo.authservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendOtpMail_shouldSendEmail() {

        emailService.sendOtpMail("test@mail.com", "123456", "REGISTER");

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        // ✅ Assertions
        assert message.getTo()[0].equals("test@mail.com");
        assert message.getSubject().contains("REGISTER");
        assert message.getText().contains("123456");
    }
}