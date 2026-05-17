package com.ecotravel.maintenance.repository;

import com.ecotravel.maintenance.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findBySpecialty(String specialty);
}