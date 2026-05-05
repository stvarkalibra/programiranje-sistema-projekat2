package org.raflab.guideservice.utils;

import org.raflab.guideservice.dtoLayer.request.GuideProfileRequest;
import org.raflab.guideservice.dtoLayer.response.GuideProfileResponse;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.springframework.stereotype.Component;

@Component
public class GuideProfileEntityMapper {

    public GuideProfile toEntity(GuideProfileRequest dto) {
        if (dto == null) return null;

        return GuideProfile.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .bio(dto.getBio())
                .contactEmail(dto.getContactEmail())
                .phoneNumber(dto.getPhoneNumber())
                .rating(0.0) // Početni rejting za novog vodiča
                .build();
    }

    public GuideProfileResponse toResponse(GuideProfile entity) {
        if (entity == null) return null;

        GuideProfileResponse response = new GuideProfileResponse();
        response.setId(entity.getId());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setBio(entity.getBio());
        response.setContactEmail(entity.getContactEmail());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setRating(entity.getRating());

        return response;
    }
}