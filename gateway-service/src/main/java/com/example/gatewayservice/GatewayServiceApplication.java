package com.example.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

import java.security.Security;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {

    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        Security.setProperty("networkaddress.cache.ttl" , "0");
        Security.setProperty("networkaddress.cache.negative.ttl" , "0");
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}
