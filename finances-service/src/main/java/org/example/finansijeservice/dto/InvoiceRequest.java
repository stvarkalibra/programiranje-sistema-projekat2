package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finansijeservice.model.InvoiceStatus;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new invoice")
public class InvoiceRequest {

    @NotNull(message = "Booking ID is required")
    @Schema(description = "ID of the booking this invoice is for", example = "1")
    private Long bookingId;

    @Schema(description = "Invoice issue date (defaults to current date if not provided)", example = "2026-04-01")
    private Date date;

    @Schema(description = "Initial invoice status (defaults to UNPAID if not provided)", example = "UNPAID",
            allowableValues = {"UNPAID", "PAID", "CANCELLED", "OVERDUE"})
    private InvoiceStatus status;
}
