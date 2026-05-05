package org.raflab.guideservice.dtoLayer.response;

import lombok.Data;

@Data
public class LanguageResponse {
    private Long id;
    private String languageName;
    private String languageCode;
}