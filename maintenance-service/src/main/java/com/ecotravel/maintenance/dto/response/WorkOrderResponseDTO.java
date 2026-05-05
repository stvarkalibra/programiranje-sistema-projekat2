package com.ecotravel.maintenance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Podaci o radnom nalogu")
public class WorkOrderResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Long unitId;
    private WorkerResponseDTO worker;
    private MaintenanceTaskResponseDTO task;
    private String status;
    private LocalDateTime scheduledFor;
    private LocalDateTime completedAt;

    @Schema(description = "Procijenjeno trajanje zadatka u satima (preuzeto iz tipa zadatka)")
    private Integer estimatedDurationHours;
}