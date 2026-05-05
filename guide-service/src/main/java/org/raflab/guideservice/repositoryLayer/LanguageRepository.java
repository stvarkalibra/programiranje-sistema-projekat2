package org.raflab.guideservice.repositoryLayer;

import org.raflab.guideservice.entityLayer.GuideProfile;
import org.raflab.guideservice.entityLayer.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    boolean existsByLanguageName(String languageName);

    boolean existsByLanguageCode(String languageCode);
}
