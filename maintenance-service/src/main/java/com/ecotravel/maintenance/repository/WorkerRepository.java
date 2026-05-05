package com.ecotravel.maintenance.repository;

import com.ecotravel.maintenance.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    // Filtriranje po specijalnosti
    List<Worker> findBySpecialty(String specialty);
}