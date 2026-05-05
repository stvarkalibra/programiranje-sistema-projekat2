package org.raflab.avanturaservice.utils;

import org.raflab.avanturaservice.dtoLayer.request.AdventureCategoryRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureCategoryResponse;
import org.raflab.avanturaservice.entityLayer.AdventureCategory;
import org.springframework.stereotype.Component;

@Component
public class AdventureCategoryEntityMapper {

    public AdventureCategory toEntity(AdventureCategoryRequest dto) {
        if (dto == null) return null;
        return AdventureCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public AdventureCategoryResponse toResponse(AdventureCategory entity) {
        if (entity == null) return null;
        AdventureCategoryResponse res = new AdventureCategoryResponse();
        res.setId(entity.getId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        // Mapiramo broj avantura ako lista nije null
        res.setNumberOfAdventures(entity.getAdventureList() != null ? entity.getAdventureList().size() : 0);
        return res;
    }
}