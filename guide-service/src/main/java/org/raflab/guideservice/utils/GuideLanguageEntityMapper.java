package org.raflab.guideservice.utils;

import org.raflab.guideservice.dtoLayer.request.GuideLanguageRequest;
import org.raflab.guideservice.dtoLayer.response.GuideLanguageResponse;
import org.raflab.guideservice.entityLayer.GuideLanguage;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.raflab.guideservice.entityLayer.Language;
import org.springframework.stereotype.Component;

@Component
public class GuideLanguageEntityMapper {

    public GuideLanguage toEntity(GuideLanguageRequest dto, GuideProfile guide, Language language) {
        if (dto == null) return null;

        return GuideLanguage.builder()
                .guideProfile(guide)
                .language(language)
                .proficiencyLevel(dto.getProficiencyLevel())
                .build();
    }

    public GuideLanguageResponse toResponse(GuideLanguage entity) {
        if (entity == null) return null;

        GuideLanguageResponse response = new GuideLanguageResponse();
        response.setId(entity.getId());
        response.setProficiencyLevel(entity.getProficiencyLevel());

        if (entity.getGuideProfile() != null) {
            response.setGuideFullName(entity.getGuideProfile().getFirstName() + " " + entity.getGuideProfile().getLastName());
        }

        if (entity.getLanguage() != null) {
            response.setLanguageName(entity.getLanguage().getLanguageName());
        }

        return response;
    }
}