package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Sumarni podaci o smjestajnoj jedinici (za medjuservisnu komunikaciju)")
public class UnitSummaryResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String name;
    private String unitType;
    private Double basePricePerNight;
    private Double totalPriceWithFacilities;
    private String city;
    private String streetAddress;
    private List<String> facilityNames;
    private Boolean isAvailable;
}