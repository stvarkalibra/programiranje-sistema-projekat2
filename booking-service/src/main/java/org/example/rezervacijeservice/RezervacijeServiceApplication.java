package org.example.rezervacijeservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(servers = {
        @Server(url = "http://localhost:8080/api/booking", description = "Gateway URL")
})
public class RezervacijeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RezervacijeServiceApplication.class, args);
    }

}
