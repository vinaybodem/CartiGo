package com.cartigo.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients
@SpringBootApplication
public class CartigoPaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoPaymentServiceApplication.class, args);
    }
}
