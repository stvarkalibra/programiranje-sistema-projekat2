package org.raflab.avanturaservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "DTO za kreiranje i izmenu zahteva za opremom")
public class GearRequirementRequest {

    @NotBlank(message = "Naziv opreme ne sme biti prazan")
    @Size(min = 2, max = 100, message = "Naziv opreme mora imati između 2 i 100 karaktera")
    @Schema(example = "Neoprensko odelo")
    private String itemName;

    @NotNull(message = "Morate naznačiti da li je oprema obavezna")
    @Schema(example = "true")
    private Boolean isMandatory; // Koristimo Boolean (objekat) da bi @NotNull radio

    @NotNull(message = "ID avanture je obavezan")
    @Schema(example = "1")
    private Long adventureId;
}