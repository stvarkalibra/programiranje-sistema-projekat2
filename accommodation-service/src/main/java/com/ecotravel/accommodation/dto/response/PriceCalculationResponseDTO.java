package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Rezultat kalkulacije cijene za odredjeni period")
public class PriceCalculationResponseDTO {

    private Long unitId;
    private String unitName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfNights;

    @Schema(description = "Osnovna cijena po nocenju")
    private Double basePricePerNight;

    @Schema(description = "Ukupni dodatak za sadrzaje")
    private Double facilitiesExtraCost;

    @Schema(description = "Aktivni sezonski mnozac (1.0 ako nema aktivne sezone)")
    private Double seasonalMultiplier;

    @Schema(description = "Naziv aktivne sezone (null ako nema)")
    private String activeSeason;

    @Schema(description = "Konacna ukupna cijena za cijeli period")
    private Double totalPrice;
}