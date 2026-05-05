package com.ecotravel.maintenance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Podaci o tipu zadatka odrzavanja")
public class MaintenanceTaskResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String taskName;
    private Integer estimatedDurationHours;
}