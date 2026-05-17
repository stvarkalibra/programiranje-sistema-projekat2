package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
}