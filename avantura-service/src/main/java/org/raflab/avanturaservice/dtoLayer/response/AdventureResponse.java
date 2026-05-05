package org.raflab.avanturaservice.dtoLayer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO za prikaz podataka o avanturi")
public class AdventureResponse {
    private Long id;
    private String title;
    private String description;
    private String difficultyLevel;
    private double basePrice;
    private Long categoryId;
    private String categoryName;
}