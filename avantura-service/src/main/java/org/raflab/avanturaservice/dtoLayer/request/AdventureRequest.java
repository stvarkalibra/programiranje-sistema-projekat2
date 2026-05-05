package org.raflab.avanturaservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "DTO za kreiranje i izmenu avanture")
public class AdventureRequest {

    @NotBlank(message = "Naslov je obavezan")
    @Schema(example = "Tara Rafting")
    private String title;

    @Size(min = 10, max = 500, message = "Opis mora imati između 10 i 500 karaktera")
    @Schema(example = "Uzbudljivo spuštanje niz reku Taru sa profesionalnim vodičima.")
    private String description;

    @Pattern(regexp = "Easy|Medium|Hard|Extreme", message = "Težina mora biti: Easy, Medium, Hard ili Extreme")
    @Schema(example = "Hard")
    private String difficultyLevel;

    @Positive(message = "Cena mora biti veća od nule")
    @Schema(example = "120.50")
    private double basePrice;

    @NotNull(message = "ID kategorije je obavezan")
    @Schema(example = "1")
    private Long categoryId;
}