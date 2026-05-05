package com.ecotravel.maintenance.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

// Ignorisem sva polja koja kolega salje a meni ne trebaju
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AccommodationUnitDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    // Kolega zove "unitType" a ja interno kako god
    @JsonProperty("unitType")
    private String unitType;

    @JsonProperty("basePricePerNight")
    private Double basePricePerNight;

    @JsonProperty("totalPriceWithFacilities")
    private Double totalPriceWithFacilities;

    @JsonProperty("city")
    private String city;

    @JsonProperty("streetAddress")
    private String streetAddress;

    @JsonProperty("facilityNames")
    private List<String> facilityNames;

    // null = nije provjereno, true = slobodna, false = zauzeta
    @JsonProperty("isAvailable")
    private Boolean isAvailable;
}