package org.raflab.avanturaservice.entityLayer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GearRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private boolean isMandatory;

    @ManyToOne
    private Adventure adventure;
}