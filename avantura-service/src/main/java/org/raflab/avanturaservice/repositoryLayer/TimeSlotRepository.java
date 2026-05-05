package org.raflab.avanturaservice.repositoryLayer;

import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
