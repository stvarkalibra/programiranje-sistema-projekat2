package org.example.rezervacijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cancellation policy details returned from the API")
public class CancellationPolicyResponse {

    @Schema(description = "Cancellation policy ID", example = "1")
    private Long id;

    @NotBlank(message = "Policy name is required")
    @Schema(description = "Name of the cancellation policy", example = "Flexible")
    private String policyName;

    @NotNull(message = "Refund percentage is required")
    @Min(value = 0, message = "Refund percentage cannot be negative")
    @Max(value = 100, message = "Refund percentage cannot exceed 100")
    @Schema(description = "Percentage of the booking amount refunded upon cancellation", example = "100")
    private Integer refundPercentage;
}
