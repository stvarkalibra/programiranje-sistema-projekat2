package com.ecotravel.accommodation.repository;

import com.ecotravel.accommodation.model.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, Long> {

    List<AvailabilityCalendar> findByUnit_Id(Long unitId);

    // Provjera da li je jedinica zauzeta u datom periodu
    @Query("SELECT COUNT(a) > 0 FROM AvailabilityCalendar a WHERE a.unit.id = :unitId " +
            "AND a.startDate <= :endDate AND a.endDate >= :startDate")
    boolean existsOverlappingBooking(
            @Param("unitId") Long unitId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}