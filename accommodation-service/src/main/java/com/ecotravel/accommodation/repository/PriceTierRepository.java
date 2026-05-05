package com.ecotravel.accommodation.repository;

import com.ecotravel.accommodation.model.PriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceTierRepository extends JpaRepository<PriceTier, Long> {

    List<PriceTier> findByUnit_Id(Long unitId);
}