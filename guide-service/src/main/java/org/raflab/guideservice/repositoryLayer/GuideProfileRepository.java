package org.raflab.guideservice.repositoryLayer;

import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideProfileRepository extends JpaRepository<GuideProfile, Long> {
    boolean existsByContactEmail(String contactEmail);

    // Pronalazi vodiče koji imaju rejting iznad proseka svih vodiča (Matematika/Statistika)
    @Query("SELECT g FROM GuideProfile g WHERE g.rating > (SELECT AVG(g2.rating) FROM GuideProfile g2)")
    List<GuideProfile> findEliteGuides();

    // Pronalazi sve vodiče koji govore određeni jezik (JOIN logika)
    @Query("SELECT gl.guideProfile FROM GuideLanguage gl WHERE gl.language.id = :languageId")
    List<GuideProfile> findAllByLanguageId(@Param("languageId") Long languageId);
}
