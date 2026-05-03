package org.example.rezervacijeservice.repository;

import org.example.rezervacijeservice.model.BookingStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingStatusLogsRepository extends JpaRepository<BookingStatusLog, Long> {
}
