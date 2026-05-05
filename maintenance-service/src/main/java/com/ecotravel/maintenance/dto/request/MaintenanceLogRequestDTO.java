package com.ecotravel.maintenance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Podaci za kreiranje zapisa u historiji odrzavanja")
public class MaintenanceLogRequestDTO {

    @NotNull(message = "ID smjestajne jedinice je obavezan")
    @Schema(description = "ID smjestajne jedinice", example = "1")
    private Long unitId;

    @NotBlank(message = "Opis zadatka je obavezan")
    @Schema(description = "Opis izvedenog zadatka", example = "Zamjena elektricnih osiguraca u glavnoj kutiji")
    private String taskDescription;

    @NotBlank(message = "Ime radnika je obavezno")
    @Schema(description = "Ime i prezime radnika koji je izveo zadatak", example = "Marko Petrovic")
    private String workerName;

    @NotNull(message = "Datum zavrsetka je obavezan")
    @Schema(description = "Datum i vrijeme zavrsetka radova", example = "2025-05-20T14:30:00")
    private LocalDateTime completedAt;

    @Schema(description = "Dodatne napomene ili komentari", example = "Promijenjeni osiguraci 16A i 25A. Preporucuje se inspekcija za 6 mjeseci.")
    private String notes;
}