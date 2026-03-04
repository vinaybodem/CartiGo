package com.cartigo.inventoryservice.config;

import com.cartigo.inventoryservice.dao.AuthPrinciple;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        // Filter should run ONLY for create endpoint
        return !path.startsWith("/api/inventory/create");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.extractAllClaims(token);

            Long sellerId = jwtUtil.extractUserId(token);
            AuthPrinciple auth = new AuthPrinciple(
                    sellerId,
                    claims.getSubject(),
                    Collections.emptyList()
            );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                auth, null, Collections.emptyList()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);


        } catch (Exception e) {
            // invalid token — let Spring Security handle or ignore
            System.out.printf(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
