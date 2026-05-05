package com.ecotravel.maintenance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Podaci za kreiranje novog radnog naloga")
public class WorkOrderRequestDTO {

    @NotNull(message = "ID smjestajne jedinice je obavezan")
    @Schema(description = "ID smjestajne jedinice (iz accommodation-service)", example = "1")
    private Long unitId;

    @NotNull(message = "ID radnika je obavezan")
    @Schema(description = "ID radnika", example = "1")
    private Long workerId;

    @NotNull(message = "ID zadatka je obavezan")
    @Schema(description = "ID tipa zadatka", example = "1")
    private Long taskId;

    @NotNull(message = "Datum planiranog izvodjenja je obavezan")
    @Schema(description = "Datum i vrijeme planiranog izvodjenja radova", example = "2025-06-15T09:00:00")
    private LocalDateTime scheduledFor;
}