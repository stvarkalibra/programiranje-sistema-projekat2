package com.ecotravel.accommodation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Podaci za kreiranje nove smjestajne jedinice")
public class UnitRequestDTO {

    @NotBlank(message = "Naziv jedinice je obavezan")
    @Schema(description = "Naziv smjestajne jedinice", example = "Planinska koliba Zeleni mir")
    private String name;

    @NotBlank(message = "Tip smjestaja je obavezan")
    @Schema(description = "Tip smjestajne jedinice", example = "CABIN",
            allowableValues = {"TENT", "CABIN", "TREEHOUSE", "GLAMPING", "ECO_LODGE"})
    private String unitType;

    @NotNull(message = "Cijena po nocenju je obavezna")
    @Positive(message = "Cijena mora biti pozitivna")
    @Schema(description = "Osnovna cijena po nocenju u eurima", example = "75.0")
    private Double basePricePerNight;

    @Schema(description = "Adresa smjestajne jedinice")
    private AddressRequestDTO address;

    @Schema(description = "Lista ID-jeva sadrzaja koje jedinica ima", example = "[1, 2, 3]")
    private List<Long> facilityIds;
}