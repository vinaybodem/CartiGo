package com.cartigo.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CartigoReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoReviewServiceApplication.class, args);
    }
}
