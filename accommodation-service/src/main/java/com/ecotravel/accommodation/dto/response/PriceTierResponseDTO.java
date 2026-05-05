package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Podaci o sezonskom cjenovniku")
public class PriceTierResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Long unitId;
    private String unitName;
    private String name;
    private Double multiplier;
    private LocalDate startDate;
    private LocalDate endDate;

    @Schema(description = "Izracunata cijena po nocenju za ovu sezonu")
    private Double calculatedPricePerNight;
}