package org.raflab.guideservice.entityLayer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(length = 1000)
    private String bio;

    private Double rating; // Prosečan rejting vodiča

    private String contactEmail;
    private String phoneNumber;

    @OneToMany(mappedBy = "guideProfile", cascade = CascadeType.ALL)
    private List<Certification> certifications;

    @OneToMany(mappedBy = "guideProfile")
    private List<GuideLanguage> guideLanguages;
}
