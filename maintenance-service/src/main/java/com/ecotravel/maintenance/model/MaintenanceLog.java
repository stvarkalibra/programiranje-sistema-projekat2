package com.ecotravel.maintenance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable=true jer sistemski logovi (SYSTEM events) možda nemaju unitId
    @Column(name = "unit_id", nullable = true)
    private Long unitId;

    @Column(name = "task_description", nullable = false)
    private String taskDescription;

    @Column(name = "worker_name", nullable = false)
    private String workerName;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}