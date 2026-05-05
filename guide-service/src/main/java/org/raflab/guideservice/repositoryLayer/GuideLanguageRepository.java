package org.raflab.guideservice.repositoryLayer;

import org.raflab.guideservice.entityLayer.GuideLanguage;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideLanguageRepository extends JpaRepository<GuideLanguage, Long> {
    // Broji koliko jezika govori određeni vodič
    long countByGuideProfileId(Long guideId);

    // Pronalazi specifičan jezik za određenog vodiča da bismo proverili nivo (B2, C1...)
    Optional<GuideLanguage> findByGuideProfileIdAndLanguageId(Long guideId, Long languageId);
}
