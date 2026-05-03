package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tax rate information")
public class TaxRateDTO {

    @Schema(description = "Tax rate ID", example = "1")
    private Long id;

    @NotBlank(message = "Tax name is required")
    @Schema(description = "Name of the tax", example = "VAT")
    private String taxName;

    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "Percentage must be at least 0")
    @Schema(description = "Tax percentage applied to the invoice", example = "20.0")
    private Double percentage;
}
