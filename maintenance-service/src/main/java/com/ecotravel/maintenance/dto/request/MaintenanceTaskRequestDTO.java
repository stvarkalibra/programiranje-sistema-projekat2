package com.ecotravel.maintenance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Podaci za kreiranje novog tipa zadatka odrzavanja")
public class MaintenanceTaskRequestDTO {

    @NotBlank(message = "Naziv zadatka je obavezan")
    @Schema(description = "Naziv zadatka", example = "Generalno ciscenje")
    private String taskName;

    @NotNull(message = "Procijenjeno trajanje je obavezno")
    @Positive(message = "Trajanje mora biti pozitivno")
    @Schema(description = "Procijenjeno trajanje u satima", example = "3")
    private Integer estimatedDurationHours;
}