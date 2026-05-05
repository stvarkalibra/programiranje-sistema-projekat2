package com.ecotravel.maintenance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Podaci za kreiranje novog radnika")
public class WorkerRequestDTO {

    @NotBlank(message = "Ime radnika je obavezno")
    @Schema(description = "Ime radnika", example = "Marko")
    private String firstName;

    @NotBlank(message = "Prezime radnika je obavezno")
    @Schema(description = "Prezime radnika", example = "Petrovic")
    private String lastName;

    @NotBlank(message = "Specijalnost je obavezna")
    @Schema(description = "Specijalnost radnika", example = "ELECTRICIAN",
            allowableValues = {"CLEANER", "ELECTRICIAN", "PLUMBER", "HVAC", "HANDYMAN", "GARDENER"})
    private String specialty;
}