package org.example.rezervacijeservice.repository;

import org.example.rezervacijeservice.model.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationPoliciesRepository extends JpaRepository<CancellationPolicy, Long> {
}
