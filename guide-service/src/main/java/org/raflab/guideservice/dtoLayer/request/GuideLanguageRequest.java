package org.raflab.guideservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO za dodeljivanje jezika vodiču")
public class GuideLanguageRequest {

    @NotNull(message = "ID vodiča je obavezan")
    @Schema(example = "1")
    private Long guideProfileId;

    @NotNull(message = "ID jezika je obavezan")
    @Schema(example = "2")
    private Long languageId;

    @Pattern(regexp = "A1|A2|B1|B2|C1|C2|Native", message = "Nivo mora biti: A1, A2, B1, B2, C1, C2 ili Native")
    @Schema(example = "C1")
    private String proficiencyLevel;
}
