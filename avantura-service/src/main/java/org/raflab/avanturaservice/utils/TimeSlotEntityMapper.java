package org.raflab.avanturaservice.utils;

import org.raflab.avanturaservice.dtoLayer.request.TimeSlotRequest;
import org.raflab.avanturaservice.dtoLayer.response.TimeSlotResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotEntityMapper {

    public TimeSlot toEntity(TimeSlotRequest dto, Adventure adventure) {
        if (dto == null) return null;
        return TimeSlot.builder()
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .maxParticipants(dto.getMaxParticipants())
                .currentOccupancy(0) // Svaki novi termin kreće sa 0 ljudi
                .adventure(adventure)
                .build();
    }

    public TimeSlotResponse toResponse(TimeSlot entity) {
        if (entity == null) return null;
        TimeSlotResponse res = new TimeSlotResponse();
        res.setId(entity.getId());
        res.setStartTime(entity.getStartTime());
        res.setEndTime(entity.getEndTime());
        res.setMaxParticipants(entity.getMaxParticipants());
        res.setCurrentOccupancy(entity.getCurrentOccupancy());
        if (entity.getAdventure() != null) {
            res.setAdventureId(entity.getAdventure().getId());
            res.setAdventureTitle(entity.getAdventure().getTitle());
        }
        return res;
    }
}
