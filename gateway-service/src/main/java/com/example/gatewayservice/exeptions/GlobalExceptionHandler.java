package com.example.gatewayservice.exeptions;


import com.example.gatewayservice.dto.ErrorResponseDTO;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired(required = false)
    private Tracer tracer;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
        String traceId = (tracer.currentSpan() != null)
                ? tracer.currentSpan().context().traceId()
                : "N/A";

        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                traceId,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
