package org.raflab.guideservice.entityLayer;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GuideProfile guideProfile;

    @ManyToOne
    private Language language;

    private String proficiencyLevel; // npr. "Native", "B2", "C1"

    @ManyToMany(mappedBy = "guideLanguage")
    private List<Certification> certifications;
}