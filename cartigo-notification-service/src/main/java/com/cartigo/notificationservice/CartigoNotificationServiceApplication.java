package com.cartigo.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableAspectJAutoProxy
public class CartigoNotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartigoNotificationServiceApplication.class, args);
    }
}
