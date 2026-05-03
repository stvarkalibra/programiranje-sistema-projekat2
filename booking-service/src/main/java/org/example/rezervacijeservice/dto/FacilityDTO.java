package org.example.rezervacijeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDTO {
    @JsonProperty("facility_id")
    private Long facilityId;
    private String name;
    @JsonProperty("extra_cost")
    private double extraCost;
}
