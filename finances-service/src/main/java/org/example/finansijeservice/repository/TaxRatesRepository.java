package org.example.finansijeservice.repository;

import org.example.finansijeservice.model.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRatesRepository extends JpaRepository<TaxRate, Long> {
}
