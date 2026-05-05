package org.raflab.guideservice.clientLayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteAdventureDTO {
    private Long id;
    private String title;
    private String difficultyLevel;
    private double basePrice;
}