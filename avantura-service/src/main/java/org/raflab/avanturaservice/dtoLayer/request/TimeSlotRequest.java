package org.raflab.avanturaservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO za kreiranje termina avanture")
public class TimeSlotRequest {

    @NotNull(message = "Vreme početka je obavezno")
    @Future(message = "Vreme početka mora biti u budućnosti")
    @Schema(example = "2026-06-01T10:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "Vreme završetka je obavezno")
    @Future(message = "Vreme završetka mora biti u budućnosti")
    @Schema(example = "2026-06-01T14:00:00")
    private LocalDateTime endTime;

    @Min(value = 1, message = "Minimalan broj učesnika je 1")
    @Max(value = 100, message = "Maksimalan broj učesnika je 100")
    @Schema(example = "12")
    private int maxParticipants;

    @NotNull(message = "ID avanture je obavezan")
    @Schema(example = "1")
    private Long adventureId;
}