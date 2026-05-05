package org.raflab.guideservice.utils;

import org.raflab.guideservice.dtoLayer.request.LanguageRequest;
import org.raflab.guideservice.dtoLayer.response.LanguageResponse;
import org.raflab.guideservice.entityLayer.Language;
import org.springframework.stereotype.Component;

@Component
public class LanguageEntityMapper {

    public Language toEntity(LanguageRequest dto) {
        if (dto == null) return null;

        return Language.builder()
                .languageName(dto.getLanguageName())
                .languageCode(dto.getLanguageCode())
                .build();
    }

    public LanguageResponse toResponse(Language entity) {
        if (entity == null) return null;

        LanguageResponse response = new LanguageResponse();
        response.setId(entity.getId());
        response.setLanguageName(entity.getLanguageName());
        response.setLanguageCode(entity.getLanguageCode());

        return response;
    }
}