package org.raflab.avanturaservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "DTO za kreiranje i izmenu recenzije avanture")
public class AdventureReviewRequest {

    @NotNull(message = "Ocena je obavezna")
    @Min(value = 1, message = "Minimalna ocena je 1")
    @Max(value = 5, message = "Maksimalna ocena je 5")
    @Schema(description = "Ocena avanture od 1 do 5", example = "5")
    private Integer rating;

    @NotBlank(message = "Komentar ne sme biti prazan")
    @Size(max = 1000, message = "Komentar može imati maksimalno 1000 karaktera")
    @Schema(description = "Tekstualni utisak o avanturi", example = "Fenomenalno iskustvo, vodiči su bili veoma profesionalni!")
    private String comment;

    @NotNull(message = "ID avanture je obavezan")
    @Schema(description = "ID avanture na koju se recenzija odnosi", example = "1")
    private Long adventureId;
}