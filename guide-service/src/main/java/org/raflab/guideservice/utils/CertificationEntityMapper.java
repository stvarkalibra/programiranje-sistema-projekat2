package org.raflab.guideservice.utils;

import org.raflab.guideservice.dtoLayer.request.CertificationRequest;
import org.raflab.guideservice.dtoLayer.response.CertificationResponse;
import org.raflab.guideservice.entityLayer.Certification;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CertificationEntityMapper {

    public Certification toEntity(CertificationRequest dto, GuideProfile guide) {
        if (dto == null) return null;

        return Certification.builder()
                .name(dto.getName())
                .issuingBody(dto.getIssuingBody())
                .expiryDate(dto.getExpiryDate())
                .guideProfile(guide)
                .build();
    }

    public CertificationResponse toResponse(Certification entity) {
        if (entity == null) return null;

        CertificationResponse response = new CertificationResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setIssuingBody(entity.getIssuingBody());
        response.setExpiryDate(entity.getExpiryDate());

        // Logička kalkulacija: poredimo datum isteka sa današnjim datumom
        response.setExpired(entity.getExpiryDate().isBefore(LocalDate.now()));

        if (entity.getGuideProfile() != null) {
            response.setGuideFullName(entity.getGuideProfile().getFirstName() + " " + entity.getGuideProfile().getLastName());
        }

        return response;
    }
}