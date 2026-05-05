package org.raflab.avanturaservice.dtoLayer.response;

import lombok.Data;

@Data
public class GearRequirementResponse {
    private Long id;
    private String itemName;
    private boolean isMandatory;
    private Long adventureId;
    private String adventureTitle;
}