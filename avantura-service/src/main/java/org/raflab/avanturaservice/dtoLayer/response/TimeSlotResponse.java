package org.raflab.avanturaservice.dtoLayer.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimeSlotResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxParticipants;
    private int currentOccupancy;
    private Long adventureId;
    private String adventureTitle;
}