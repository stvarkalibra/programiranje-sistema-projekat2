package com.ecotravel.accommodation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Dodatna cijena koju ovaj sadrzaj dodaje (0.0 ako je besplatan)
    @Column(name = "extra_cost", nullable = false)
    private Double extraCost = 0.0;

    @ManyToMany(mappedBy = "facilities")
    private List<Unit> units;
}