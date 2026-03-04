package com.cartigo.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CartigoCartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoCartServiceApplication.class, args);
    }
}
