package com.ecotravel.maintenance.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccommodationErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign greska iz accommodation servisa: status={}, metoda={}",
                response.status(), methodKey);

        return switch (response.status()) {
            case 404 -> new RuntimeException(
                    "Smjestajna jedinica nije pronadjena u accommodation servisu");
            case 400 -> new IllegalArgumentException(
                    "Nevalidan zahtjev prema accommodation servisu");
            case 409 -> new IllegalStateException(
                    "Konflikt podataka u accommodation servisu");
            case 503 -> new RuntimeException(
                    "Accommodation servis trenutno nije dostupan");
            default -> new RuntimeException(
                    "Greska u komunikaciji sa accommodation servisom (status: " + response.status() + ")");
        };
    }
}