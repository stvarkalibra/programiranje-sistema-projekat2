package org.example.rezervacijeservice.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends RuntimeException {

    private final HttpStatus status;

    public RateLimitException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
