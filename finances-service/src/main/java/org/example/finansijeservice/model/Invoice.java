package org.example.finansijeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "invoice")
    private List<Transaction> transactions;
    @ManyToOne
    @JoinColumn(name="tax_rate_id")
    private TaxRate taxRate;
    private Long bookingId;
    private Double subtotal;
    private Double totalAmount;
    @Column(name = "arrival_date")
    private Date date;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

}
