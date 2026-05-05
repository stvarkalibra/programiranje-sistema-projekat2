package com.ecotravel.accommodation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Podaci za kreiranje sezonskog cjenovnika")
public class PriceTierRequestDTO {

    @NotNull(message = "ID smjestajne jedinice je obavezan")
    @Schema(description = "ID smjestajne jedinice", example = "1")
    private Long unitId;

    @NotBlank(message = "Naziv sezone je obavezan")
    @Schema(description = "Naziv sezone ili tarife", example = "Ljetna sezona")
    private String name;

    @NotNull(message = "Mnozac cijene je obavezan")
    @Positive(message = "Mnozac mora biti pozitivan")
    @Schema(description = "Mnozac cijene (npr. 1.5 = 50% skuplje)", example = "1.5")
    private Double multiplier;

    @NotNull(message = "Datum pocetka je obavezan")
    @Schema(description = "Datum pocetka sezone", example = "2025-07-01")
    private LocalDate startDate;

    @NotNull(message = "Datum kraja je obavezan")
    @Schema(description = "Datum kraja sezone", example = "2025-08-31")
    private LocalDate endDate;
}