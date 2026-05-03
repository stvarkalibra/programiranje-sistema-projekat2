package org.example.rezervacijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Add-on service item returned from the API")
public class AddOnItemResponse {

    @Schema(description = "Add-on item ID", example = "1")
    private Long id;

    @NotBlank(message = "Service name is required")
    @Schema(description = "Name of the additional service", example = "Airport transfer")
    private String serviceName;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "Price of the additional service", example = "25.00")
    private Double price;
}
