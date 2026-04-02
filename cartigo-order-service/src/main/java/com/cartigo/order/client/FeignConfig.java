package com.cartigo.order.client;


import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    /**
     * Forward Authorization header to downstream services
     */
    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            String authorization = request.getHeader("Authorization");

            if (authorization != null) {
                requestTemplate.header("Authorization", authorization);
            }
        };
    }

    /**
     * Feign logging level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
