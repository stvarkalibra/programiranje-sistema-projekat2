package com.ecotravel.maintenance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Podaci o radniku")
public class WorkerResponseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private String firstName;
    private String lastName;
    private String specialty;

    @Schema(description = "Puno ime radnika")
    private String fullName;
}