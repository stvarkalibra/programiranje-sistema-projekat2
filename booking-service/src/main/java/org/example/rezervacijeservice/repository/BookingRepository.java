package org.example.rezervacijeservice.repository;

import org.example.rezervacijeservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Date startDate,
            Date endDate
    );
}