package com.ecotravel.maintenance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "maintenance_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    // Procijenjeno trajanje u satima
    @Column(name = "estimated_duration_hours", nullable = false)
    private Integer estimatedDurationHours;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<WorkOrder> workOrders;
}