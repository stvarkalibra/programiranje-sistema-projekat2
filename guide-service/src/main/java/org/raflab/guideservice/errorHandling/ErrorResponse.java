package org.raflab.guideservice.errorHandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private String traceId;
    private LocalDateTime timestamp;
}