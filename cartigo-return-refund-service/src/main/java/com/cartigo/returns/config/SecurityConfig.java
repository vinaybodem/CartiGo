package com.cartigo.returns.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * In microservices, JWT validation usually happens at API Gateway.
 * This service can run with permitAll=true for dev / internal traffic.
 */
@Configuration
public class SecurityConfig {

    @Value("${app.security.permit-all:true}")
    private boolean permitAll;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ REQUIRED for WebConfig CORS to work with Spring Security
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> {
            // ✅ Always allow swagger + actuator
            auth.requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/health",
                    "/actuator/info"
            ).permitAll();

            if (permitAll) auth.anyRequest().permitAll();
            else auth.anyRequest().authenticated();
        });

        return http.build();
    }
}
