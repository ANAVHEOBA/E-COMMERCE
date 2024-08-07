 package com.example.demo;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
@Log4j2
public class OrderServiceApp {
    public static void main(String[] args) {
        log.trace("Trace log: Entering processOrder method");
        log.info("Processing order...");
        log.debug("Order Processed successfully");
        SpringApplication.run(OrderServiceApp.class, args);
    }
}