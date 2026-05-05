package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Podaci o blokiranom periodu u kalendaru dostupnosti")
public class AvailabilityResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Long unitId;
    private String unitName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}