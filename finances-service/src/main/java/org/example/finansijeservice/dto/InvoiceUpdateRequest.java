package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finansijeservice.model.InvoiceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceUpdateRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New invoice status", example = "PAID",
            allowableValues = {"UNPAID", "PAID", "CANCELLED", "OVERDUE"})
    private InvoiceStatus status;
}
