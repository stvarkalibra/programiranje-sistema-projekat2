package org.example.rezervacijeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.Date;

@Entity
@Table(name = "booking_status_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    private String oldStatus;
    private String newStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedDate;
}
