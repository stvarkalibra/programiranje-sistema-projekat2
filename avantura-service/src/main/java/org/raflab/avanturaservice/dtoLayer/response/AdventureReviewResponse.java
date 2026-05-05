package org.raflab.avanturaservice.dtoLayer.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdventureReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long adventureId;
    private String adventureTitle;
}
