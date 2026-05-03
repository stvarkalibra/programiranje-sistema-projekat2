package org.example.rezervacijeservice.repository;

import org.example.rezervacijeservice.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestsRepository extends JpaRepository<Guest, Long> {
}
