package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.Adventure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdventureRepository extends JpaRepository<Adventure,Long> {
    // Metoda koja proverava da li naslov vec postoji (vraca true/false)
    boolean existsByTitle(String title);

    // Bonus: Metoda koja ce ti trebati za filtriranje po kategoriji i ceni (zahtev iz zadatka)
    List<Adventure> findByAdventureCategoryIdAndBasePriceLessThanEqual(Long categoryId, Double maxPrice);

    List<Adventure> findByGuideId(Long guideId);
    long countByGuideId(Long guideId);

    // [Tačka 4] Složeni upit: Sve skupe avanture određenog vodiča
    @Query("SELECT a FROM Adventure a WHERE a.guideId = :guideId AND a.basePrice > 100")
    List<Adventure> findPremiumAdventuresByGuide(@Param("guideId") Long guideId);

    @Query("SELECT a FROM Adventure a JOIN a.adventureCategory c WHERE c.id = :categoryId")
    List<Adventure> findByCategoryId(@Param("categoryId") Long categoryId);
}
