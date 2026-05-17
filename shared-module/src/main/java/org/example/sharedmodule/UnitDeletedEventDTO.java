package org.example.sharedmodule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDeletedEventDTO {

    private Long unitId;
    private String unitName;
    private LocalDateTime occurredAt;
}