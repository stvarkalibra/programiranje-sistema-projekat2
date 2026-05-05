package org.raflab.guideservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

// request/GuideProfileRequest.java
@Data
@Schema(description = "DTO za kreiranje profila vodiča")
public class GuideProfileRequest {

    @NotBlank(message = "Ime je obavezno")
    @Schema(example = "Marko")
    private String firstName;

    @NotBlank(message = "Prezime je obavezno")
    @Schema(example = "Marković")
    private String lastName;

    @Size(max = 1000)
    @Schema(example = "Iskusni planinar sa preko 10 godina iskustva u vođenju tura po Balkanu.")
    private String bio;

    @NotNull(message = "Rejting je obavezan")
    @Min(value = 1, message = "Minimalni rejting je 1")
    @Max(value = 5, message = "Maksimalni rejting je 5")
    @Schema(example = "4.5")
    private Double rating;

    @Email(message = "Email format nije ispravan")
    @NotBlank(message = "Email je obavezan")
    @Schema(example = "marko.guide@example.com")
    private String contactEmail;

    @Pattern(regexp = "^\\+?[0-9]{15,16}$", message = "Telefon mora biti u formatu +381612345678")
    @Schema(example = "+381641234567")
    private String phoneNumber;
}