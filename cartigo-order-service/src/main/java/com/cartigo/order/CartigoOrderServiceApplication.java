package com.cartigo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CartigoOrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoOrderServiceApplication.class, args);
    }
}
