package com.cartigo.authservice.client.fallback;

import com.cartigo.authservice.client.UserServiceClient;
import com.cartigo.authservice.client.dto.UserCreateFromAuthRequest;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public Object createUserFromAuth(UserCreateFromAuthRequest request) {
        throw new RuntimeException("User Service is temporarily unavailable. Circuit breaker open.");
    }
}
