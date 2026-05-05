package org.raflab.guideservice.dtoLayer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO za dodavanje sertifikata vodiču")
public class CertificationRequest {

    @NotBlank(message = "Naziv sertifikata je obavezan")
    @Schema(example = "Gorska Služba Spasavanja (GSS)")
    private String name;

    @NotBlank(message = "Izdavač sertifikata je obavezan")
    @Schema(example = "Planinarski savez Srbije")
    private String issuingBody;

    @NotNull(message = "Datum isteka je obavezan")
    @Future(message = "Datum isteka mora biti u budućnosti")
    @Schema(example = "2027-12-31")
    private LocalDate expiryDate;

    @NotNull(message = "ID vodiča je obavezan")
    @Schema(example = "1")
    private Long guideProfileId;
}
