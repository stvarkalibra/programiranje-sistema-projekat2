package org.example.finansijeservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(servers = {
        @Server(url = "http://localhost:8080", description = "Gateway URL")
})
@EnableDiscoveryClient
public class FinansijeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinansijeServiceApplication.class, args);
    }

}
