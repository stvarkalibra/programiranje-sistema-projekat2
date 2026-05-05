package com.ecotravel.accommodation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Podaci o adresi smjestajne jedinice")
public class AddressResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String city;
    private String zipCode;
    private String streetAddress;
}