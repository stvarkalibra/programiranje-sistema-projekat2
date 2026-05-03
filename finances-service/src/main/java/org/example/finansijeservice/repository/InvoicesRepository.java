package org.example.finansijeservice.repository;

import org.example.finansijeservice.model.Invoice;
import org.example.finansijeservice.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByBookingId(Long bookingId);

    List<Invoice> findByStatusAndBookingId(InvoiceStatus status, Long bookingId);

    @Query("""
    SELECT i FROM Invoice i
    JOIN i.transactions t
    WHERE i.status = 'UNPAID'
    GROUP BY i
    HAVING SUM(t.amount) < i.totalAmount
    """)
    List<Invoice> findPartiallyPaidInvoices();

    @Query("""
    SELECT i FROM Invoice i
    JOIN i.taxRate tr
    WHERE tr.taxName = :taxName
      AND i.totalAmount > :minAmount
      AND i.status <> 'CANCELLED'
    ORDER BY i.totalAmount DESC
    """)
    List<Invoice> findByTaxNameAndMinAmount(
            @Param("taxName") String taxName,
            @Param("minAmount") Double minAmount
    );

}
