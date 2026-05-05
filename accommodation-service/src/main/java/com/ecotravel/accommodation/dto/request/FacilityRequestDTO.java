package com.ecotravel.accommodation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Podaci za kreiranje novog sadrzaja")
public class FacilityRequestDTO {

    @NotBlank(message = "Naziv sadrzaja je obavezan")
    @Schema(description = "Naziv sadrzaja", example = "Privatni bazen")
    private String name;

    @NotNull(message = "Dodatna cijena je obavezna (0 ako je besplatno)")
    @PositiveOrZero(message = "Dodatna cijena ne moze biti negativna")
    @Schema(description = "Fiksni iznos koji se dodaje na osnovnu cijenu", example = "25.0")
    private Double extraCost;
}