package com.ecotravel.accommodation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Podaci o adresi smjestajne jedinice")
public class AddressRequestDTO {

    @NotBlank(message = "Grad je obavezan")
    @Schema(description = "Grad", example = "Zabljak")
    private String city;

    @NotBlank(message = "Postanski broj je obavezan")
    @Pattern(regexp = "\\d{5}", message = "Postanski broj mora imati tacno 5 cifara")
    @Schema(description = "Postanski broj", example = "84210")
    private String zipCode;

    @NotBlank(message = "Adresa je obavezna")
    @Schema(description = "Ulica i broj", example = "Durmitorska bb")
    private String streetAddress;
}