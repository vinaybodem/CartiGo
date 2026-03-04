package com.cartigo.cart.config;

import com.cartigo.cart.dto.AuthPrinciple;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        AuthPrinciple principal = (AuthPrinciple) authentication.getPrincipal();

        if(principal.getId()==null) throw new RuntimeException("Unauthorized request");
        return principal.getId();
    }
}
