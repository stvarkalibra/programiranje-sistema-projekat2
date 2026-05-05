package org.raflab.guideservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO za unos novog jezika u katalog")
public class LanguageRequest {

    @NotBlank(message = "Naziv jezika je obavezan")
    @Schema(example = "Engleski")
    private String languageName;

    @NotBlank(message = "ISO kod jezika je obavezan")
    @Size(min = 2, max = 5)
    @Schema(example = "EN")
    private String languageCode;
}
