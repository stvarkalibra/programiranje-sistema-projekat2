package com.ecotravel.maintenance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Podaci za promjenu statusa radnog naloga")
public class WorkOrderStatusUpdateDTO {

    @NotBlank(message = "Novi status je obavezan")
    @Schema(description = "Novi status radnog naloga", example = "IN_PROGRESS",
            allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"})
    private String status;
}