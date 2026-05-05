package org.raflab.guideservice.dtoLayer.response;

import lombok.Data;

@Data
public class GuideLanguageResponse {
    private Long id;
    private String guideFullName;
    private String languageName;
    private String proficiencyLevel;
}
