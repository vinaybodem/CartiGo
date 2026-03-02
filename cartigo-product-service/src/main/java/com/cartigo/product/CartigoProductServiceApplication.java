package com.cartigo.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CartigoProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartigoProductServiceApplication.class, args);
	}

}
