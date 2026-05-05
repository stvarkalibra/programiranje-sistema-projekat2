package org.raflab.avanturaservice.utils;

import org.raflab.avanturaservice.dtoLayer.request.AdventureRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.AdventureCategory;
import org.springframework.stereotype.Component;

@Component
public class AdventureEntityMapper {

    // DTO -> ENTITY
    public Adventure toEntity(AdventureRequest dto, AdventureCategory category) {
        if (dto == null) return null;

        return Adventure.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .difficultyLevel(dto.getDifficultyLevel())
                .basePrice(dto.getBasePrice())
                .adventureCategory(category)
                .build();
    }

    // ENTITY -> DTO
    public AdventureResponse toResponse(Adventure entity) {
        if (entity == null) return null;

        AdventureResponse response = new AdventureResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setDifficultyLevel(entity.getDifficultyLevel());
        response.setBasePrice(entity.getBasePrice());

        if (entity.getAdventureCategory() != null) {
            response.setCategoryId(entity.getAdventureCategory().getId());
            response.setCategoryName(entity.getAdventureCategory().getName());
        }

        return response;
    }
}