package org.example.sharedmodule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDeletedEventDTO {
    private Long timeSlotId;
    private Long adventureId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}