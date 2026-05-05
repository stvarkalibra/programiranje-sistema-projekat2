package com.ecotravel.maintenance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Podaci o zapisu u historiji odrzavanja")
public class MaintenanceLogResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Long unitId;
    private String taskDescription;
    private String workerName;
    private LocalDateTime completedAt;
    private String notes;
}