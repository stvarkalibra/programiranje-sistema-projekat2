package com.ecotravel.maintenance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "workers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Specijalnost radnika: CLEANER, ELECTRICIAN, PLUMBER, HVAC, HANDYMAN itd.
    @Column(nullable = false)
    private String specialty;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;
}