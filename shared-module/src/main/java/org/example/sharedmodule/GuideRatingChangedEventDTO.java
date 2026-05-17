package org.example.sharedmodule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideRatingChangedEventDTO {

    private Long guideId;
    private String guideFullName;
    private Double oldRating;
    private Double newRating;
    private LocalDateTime occurredAt;
}