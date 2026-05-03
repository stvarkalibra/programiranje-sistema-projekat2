package org.example.rezervacijeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="unit_id")
    private Long unitId;
    @OneToMany(mappedBy = "booking")
    private List<AddOnItem> addOnItems;
    @OneToMany(mappedBy = "booking")
    private List<BookingStatusLog> bookingStatusLogs;
    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;
    @ManyToOne
    @JoinColumn(name = "cancellation_policy_id")
    private CancellationPolicy cancellationPolicy;
    private Date startDate;
    private Date endDate;
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
