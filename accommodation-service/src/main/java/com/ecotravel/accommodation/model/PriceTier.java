package com.ecotravel.accommodation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "price_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    // Naziv sezone npr. "Ljetna sezona", "Vikend tarifa"
    @Column(nullable = false)
    private String name;

    // Mnozac cijene npr. 1.5 znaci 50% skuplje
    @Column(nullable = false)
    private Double multiplier;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}