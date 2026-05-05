package com.ecotravel.accommodation.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private int status;
    private String traceId;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int status, String traceId, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getTraceId() {
        return traceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}