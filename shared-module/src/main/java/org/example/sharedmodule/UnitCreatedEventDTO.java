package org.example.sharedmodule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitCreatedEventDTO {

    private Long unitId;
    private String unitName;
    private String unitType;
    private String city;
    private Double basePricePerNight;
    private LocalDateTime occurredAt;
}