package org.raflab.guideservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bad request from adventure service"
            );
            case 401 -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized request to adventure service"
            );
            case 403 -> new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Forbidden request to adventure service"
            );
            case 404 -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Resource not found in adventure service"
            );
            case 409 -> new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Conflict in adventure service"
            );
            case 500 -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal error in adventure service"
            );
            case 503 -> new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Adventure service unavailable"
            );
            default -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error from adventure service: " + response.status()
            );
        };
    }
}