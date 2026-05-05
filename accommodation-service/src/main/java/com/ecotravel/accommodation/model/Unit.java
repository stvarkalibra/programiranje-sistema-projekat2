package com.ecotravel.accommodation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Tip smjestaja: TENT (sator), CABIN (koliba), TREEHOUSE, GLAMPING itd.
    @Column(name = "unit_type", nullable = false)
    private String unitType;

    @Column(name = "base_price_per_night", nullable = false)
    private Double basePricePerNight;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany
    @JoinTable(
            name = "unit_facilities",
            joinColumns = @JoinColumn(name = "unit_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private List<Facility> facilities;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<PriceTier> priceTiers;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<AvailabilityCalendar> availabilityCalendars;
}