package org.example.rezervacijeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "add_on_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    private String serviceName;
    private Double price;

}
