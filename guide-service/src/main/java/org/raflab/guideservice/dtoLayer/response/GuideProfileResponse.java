package org.raflab.guideservice.dtoLayer.response;

import lombok.Data;

import java.util.List;

@Data
public class GuideProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private Double rating;
    private String contactEmail;
    private String phoneNumber;
    private List<String> languages; // Lista naziva jezika koje govori
    private List<String> activeCertifications; // Lista naziva važećih sertifikata
}
