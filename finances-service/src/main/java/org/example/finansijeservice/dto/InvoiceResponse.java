package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finansijeservice.model.InvoiceStatus;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Invoice details returned from the API")
public class InvoiceResponse {

    @Schema(description = "List of transactions associated with this invoice")
    private List<TransactionDTO> transactions;

    @Schema(description = "Tax rate applied to the invoice")
    private TaxRateDTO taxRate;

    @Schema(description = "Amount before tax", example = "100.00")
    private Double subtotal;

    @Schema(description = "Total amount including tax", example = "120.00")
    private Double totalAmount;

    @Schema(description = "Invoice issue date", example = "2026-04-01")
    private Date date;

    @Schema(description = "Current invoice status", example = "UNPAID",
            allowableValues = {"UNPAID", "PAID", "CANCELLED", "OVERDUE"})
    private InvoiceStatus status;
}
