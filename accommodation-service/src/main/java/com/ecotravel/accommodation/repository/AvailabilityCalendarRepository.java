package com.ecotravel.accommodation.repository;

import com.ecotravel.accommodation.model.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, Long> {

    List<AvailabilityCalendar> findByUnit_Id(Long unitId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AvailabilityCalendar a " +
            "WHERE a.unit.id = :unitId " +
            "AND a.startDate <= :endDate AND a.endDate >= :startDate")
    boolean existsOverlappingBooking(@Param("unitId") Long unitId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * Briše MAINTENANCE blokade za jedinicu na određeni datum.
     * Poziva se kada maintenance završi radni nalog (WorkOrderCompleted event).
     * Vraca broj obrisanih zapisa.
     */
    @Modifying
    @Query("DELETE FROM AvailabilityCalendar a " +
            "WHERE a.unit.id = :unitId " +
            "AND a.reason = :reason " +
            "AND a.startDate <= :date AND a.endDate >= :date")
    int deleteByUnitIdAndReasonAndDateRange(@Param("unitId") Long unitId,
                                            @Param("reason") String reason,
                                            @Param("date") LocalDate date);
}