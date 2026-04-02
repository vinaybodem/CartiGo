package com.cartigo.order.dto;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthPrinciple implements UserDetails {
    private final Long id;
    private final String email;
    private final List<GrantedAuthority> authorities;

    public AuthPrinciple(Long id, String email, List<GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }
}
