package org.example.rezervacijeservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String serviceName = "Eksterni servis";
        if (methodKey.contains("UnitClient")) {
            serviceName = "Smestaj servis";
        }

        return switch (response.status()) {
            case 400 -> new BadRequestException(serviceName + " : Nevalidan zahtev");
            case 401 -> new BadRequestException("Greska u autentifikaciji izmedju servisa");
            case 403 -> new BadRequestException("Zabranjen pristup resursu u " + serviceName);
            case 404 -> new ResourceNotFoundException(serviceName + " : Resurs nije pronadjen");
            case 429 -> new BadRequestException(serviceName + " je preopterecen, usporite");
            default  -> new BadRequestException(serviceName + " je vratio gresku: " + response.status());
        };
    }
}
