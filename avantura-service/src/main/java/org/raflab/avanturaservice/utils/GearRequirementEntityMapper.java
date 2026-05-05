package org.raflab.avanturaservice.utils;

import org.raflab.avanturaservice.dtoLayer.request.GearRequirementRequest;
import org.raflab.avanturaservice.dtoLayer.response.GearRequirementResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.GearRequirement;
import org.springframework.stereotype.Component;

@Component
public class GearRequirementEntityMapper {

    public GearRequirement toEntity(GearRequirementRequest dto, Adventure adventure) {
        if (dto == null) return null;
        return GearRequirement.builder()
                .itemName(dto.getItemName())
                .isMandatory(dto.getIsMandatory())
                .adventure(adventure)
                .build();
    }

    public GearRequirementResponse toResponse(GearRequirement entity) {
        if (entity == null) return null;
        GearRequirementResponse res = new GearRequirementResponse();
        res.setId(entity.getId());
        res.setItemName(entity.getItemName());
        res.setMandatory(entity.isMandatory());
        if (entity.getAdventure() != null) {
            res.setAdventureId(entity.getAdventure().getId());
            res.setAdventureTitle(entity.getAdventure().getTitle());
        }
        return res;
    }
}