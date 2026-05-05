package org.raflab.guideservice.repositoryLayer;

import org.raflab.guideservice.entityLayer.Certification;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    // Broji koliko vodič ima sertifikata koji još nisu istekli
    @Query("SELECT COUNT(c) FROM Certification c WHERE c.guideProfile.id = :guideId AND c.expiryDate > :today")
    long countValidCertifications(@Param("guideId") Long guideId, @Param("today") LocalDate today);

    // Lista svih sertifikata koji su istekli pre datog datuma
    List<Certification> findByExpiryDateBefore(LocalDate date);
}
