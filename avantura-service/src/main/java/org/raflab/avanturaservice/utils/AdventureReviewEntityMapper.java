package org.raflab.avanturaservice.utils;

import org.raflab.avanturaservice.dtoLayer.request.AdventureReviewRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureReviewResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.AdventureReview;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdventureReviewEntityMapper {

    public AdventureReview toEntity(AdventureReviewRequest dto, Adventure adventure) {
        if (dto == null) return null;
        return AdventureReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now()) // Automatsko setovanje vremena
                .adventure(adventure)
                .build();
    }

    public AdventureReviewResponse toResponse(AdventureReview entity) {
        if (entity == null) return null;
        AdventureReviewResponse res = new AdventureReviewResponse();
        res.setId(entity.getId());
        res.setRating(entity.getRating());
        res.setComment(entity.getComment());
        res.setCreatedAt(entity.getCreatedAt());
        if (entity.getAdventure() != null) {
            res.setAdventureId(entity.getAdventure().getId());
            res.setAdventureTitle(entity.getAdventure().getTitle());
        }
        return res;
    }
}
