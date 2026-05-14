package org.example.rezervacijeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitDTO {
    @JsonProperty("id")
    private Long unitId;
    private String name;
    private String unitType;
    @JsonProperty("basePricePerNight")
    private double basePrice;
    private Set<FacilityDTO> facilities = new HashSet<>();
}
