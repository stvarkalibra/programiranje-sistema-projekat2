package org.raflab.avanturaservice.dtoLayer.response;

import lombok.Data;

@Data
public class AdventureCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private int numberOfAdventures; // Dodatna informacija
}
