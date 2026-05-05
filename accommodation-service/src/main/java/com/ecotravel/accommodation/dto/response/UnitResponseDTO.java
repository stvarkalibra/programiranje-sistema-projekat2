package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Podaci o smjestajnoj jedinici")
public class UnitResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String name;
    private String unitType;
    private Double basePricePerNight;

    @Schema(description = "Ukupna cijena sa svim sadrzajima (osnovna + extra_cost svih sadrzaja)")
    private Double totalPriceWithFacilities;

    private AddressResponseDTO address;
    private List<FacilityResponseDTO> facilities;
}