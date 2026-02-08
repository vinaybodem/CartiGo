package com.cartigo.reporting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * For Power BI, easiest is to keep reporting APIs public inside your network
 * or protect via API Gateway + JWT + role=ADMIN.
 */
@Configuration
public class SecurityConfig {

    @Value("${app.security.permit-all:true}")
    private boolean permitAll;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> {
            // always allow swagger + actuator
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
