package com.ecotravel.accommodation.repository;

import com.ecotravel.accommodation.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    // Filtriranje po tipu smjestaja
    List<Unit> findByUnitType(String unitType);

    // Filtriranje po gradu (kroz adresu)
    List<Unit> findByAddress_City(String city);

    // Filtriranje po cijeni - maksimalna cijena
    List<Unit> findByBasePricePerNightLessThanEqual(Double maxPrice);

    // SLOZENI UPIT 1: JOIN sa facilities - vraca jedinice koje imaju odredjeni sadrzaj
    // i ukupnu cijenu (osnovna + suma extra_cost sadrzaja)
    @Query("SELECT u FROM Unit u JOIN u.facilities f WHERE f.name = :facilityName")
    List<Unit> findByFacilityName(@Param("facilityName") String facilityName);

    // SLOZENI UPIT 2: Agregacija - vraca jedinice sa prosjecnom cijenom sadrzaja
    // sortiranih po ukupnoj cijeni (osnovna + ukupni extra_cost svih facilities)
    @Query("SELECT u FROM Unit u LEFT JOIN u.facilities f " +
            "WHERE u.unitType = :unitType " +
            "GROUP BY u " +
            "HAVING (u.basePricePerNight + COALESCE(SUM(f.extraCost), 0)) <= :maxTotalPrice")
    List<Unit> findByTypeAndMaxTotalPrice(
            @Param("unitType") String unitType,
            @Param("maxTotalPrice") Double maxTotalPrice
    );

    // SLOZENI UPIT 3: Vraca jedinice u odredjenom gradu sa svim sadrzajima
    // (JOIN FETCH da izbjegnemo N+1 problem)
    @Query("SELECT DISTINCT u FROM Unit u LEFT JOIN FETCH u.facilities WHERE u.address.city = :city")
    List<Unit> findByCity_WithFacilities(@Param("city") String city);
}