package org.example.finansijeservice.repository;

import org.example.finansijeservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByInvoiceId(Long invoiceId);

    List<Transaction> findByPaymentMethodId(Long paymentMethodId);
}
