package com.ecotravel.maintenance.repository;

import com.ecotravel.maintenance.model.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {

    // Filtriranje po maksimalnom trajanju
    List<MaintenanceTask> findByEstimatedDurationHoursLessThanEqual(Integer maxHours);
}