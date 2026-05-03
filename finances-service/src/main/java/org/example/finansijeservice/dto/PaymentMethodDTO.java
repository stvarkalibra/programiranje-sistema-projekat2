package org.example.finansijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment method information")
public class PaymentMethodDTO {

    @Schema(description = "Payment method ID", example = "1")
    private Long id;

    @NotBlank(message = "Payment method name is required")
    @Schema(description = "Name of the payment method", example = "Credit Card")
    private String methodName;

    @NotNull(message = "Active status is required")
    @Schema(description = "Whether the payment method is currently active", example = "true")
    private Boolean isActive;
}
