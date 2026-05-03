package org.example.finansijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.finansijeservice.dto.PaymentMethodDTO;
import org.example.finansijeservice.service.PaymentMethodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@CrossOrigin(origins = "http://localhost:8080")
public class PaymentMethodsController {

    private final PaymentMethodsService paymentMethodsService;

    public PaymentMethodsController(PaymentMethodsService paymentMethodsService) {
        this.paymentMethodsService = paymentMethodsService;
    }

    @GetMapping
    @Operation(
            summary = "List payment methods",
            description = "Returns all payment methods. Optionally filter by active status."
    )
    @ApiResponse(responseCode = "200", description = "Payment methods retrieved successfully")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethods(
            @RequestParam(required = false)
            @Schema(description = "Filter by active status", example = "true")
            Boolean activeOnly) {

        List<PaymentMethodDTO> methods;
        if (Boolean.TRUE.equals(activeOnly)) {
            methods = paymentMethodsService.getActivePaymentMethods();
        } else {
            methods = paymentMethodsService.getAllPaymentMethods();
        }
        return ResponseEntity.ok(methods);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment method by ID")
    @ApiResponse(responseCode = "200", description = "Payment method retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment method not found")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodById(
            @PathVariable
            @Schema(description = "Payment method ID", example = "1")
            Long id) {
        return ResponseEntity.ok(paymentMethodsService.getPaymentMethodById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create payment method",
            description = "Creates a new payment method"
    )
    @ApiResponse(responseCode = "201", description = "Payment method created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@RequestBody @Valid PaymentMethodDTO dto) {
        PaymentMethodDTO response = paymentMethodsService.createPaymentMethod(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment method")
    @ApiResponse(responseCode = "200", description = "Payment method updated successfully")
    @ApiResponse(responseCode = "404", description = "Payment method not found")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(
            @PathVariable
            @Schema(description = "Payment method ID", example = "1")
            Long id,
            @RequestBody @Valid PaymentMethodDTO dto) {
        return ResponseEntity.ok(paymentMethodsService.updatePaymentMethod(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment method")
    @ApiResponse(responseCode = "204", description = "Payment method deleted successfully")
    @ApiResponse(responseCode = "404", description = "Payment method not found")
    public ResponseEntity<Void> deletePaymentMethod(
            @PathVariable
            @Schema(description = "Payment method ID", example = "1")
            Long id) {
        paymentMethodsService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}
