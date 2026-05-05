package org.raflab.guideservice.dtoLayer.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificationResponse {
    private Long id;
    private String name;
    private String issuingBody;
    private LocalDate expiryDate;
    private boolean expired;
    private String guideFullName;
}
