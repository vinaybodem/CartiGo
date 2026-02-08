package com.cartigo.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class CartigoInventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoInventoryServiceApplication.class, args);
    }
}
