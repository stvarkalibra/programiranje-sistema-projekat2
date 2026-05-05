package com.ecotravel.accommodation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Podaci za blokiranje datuma u kalendaru dostupnosti")
public class AvailabilityRequestDTO {

    @NotNull(message = "ID smjestajne jedinice je obavezan")
    @Schema(description = "ID smjestajne jedinice", example = "1")
    private Long unitId;

    @NotNull(message = "Datum pocetka je obavezan")
    @Schema(description = "Datum pocetka blokiranja", example = "2025-07-10")
    private LocalDate startDate;

    @NotNull(message = "Datum kraja je obavezan")
    @Schema(description = "Datum kraja blokiranja", example = "2025-07-17")
    private LocalDate endDate;

    @NotBlank(message = "Razlog blokiranja je obavezan")
    @Schema(description = "Razlog blokiranja", example = "BOOKED",
            allowableValues = {"BOOKED", "MAINTENANCE", "OWNER_USE", "RENOVATION"})
    private String reason;
}