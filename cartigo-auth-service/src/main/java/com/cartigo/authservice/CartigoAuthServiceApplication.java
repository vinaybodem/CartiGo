package com.cartigo.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CartigoAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoAuthServiceApplication.class, args);
    }
}
