package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction details")
public class TransactionDTO {

    @Schema(description = "Transaction ID", example = "1")
    private Long id;

    @NotNull(message = "Invoice ID is required")
    @Schema(description = "ID of the invoice this transaction belongs to", example = "5")
    private Long invoiceId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Schema(description = "Transaction amount", example = "120.00")
    private Double amount;

    @NotNull(message = "Transaction date is required")
    @Schema(description = "Timestamp when the transaction was processed", example = "2026-04-01T10:30:00")
    private Timestamp transactionDate;

    @NotNull(message = "Payment method ID is required")
    @Schema(description = "ID of the payment method used", example = "2")
    private Long paymentMethodId;
}
