package com.cartigo.authservice.client;

import com.cartigo.authservice.client.dto.UserCreateFromAuthRequest;
import com.cartigo.authservice.client.fallback.UserServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE", path = "/api/users",fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @PostMapping("/from-auth")
    Object createUserFromAuth(@RequestBody UserCreateFromAuthRequest request);
}
