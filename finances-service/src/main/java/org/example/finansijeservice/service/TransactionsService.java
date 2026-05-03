package org.example.finansijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.finansijeservice.dto.TransactionDTO;
import org.example.finansijeservice.exception.ResourceNotFoundException;
import org.example.finansijeservice.factory.PaymentProcessor;
import org.example.finansijeservice.factory.PaymentProcessorFactory;
import org.example.finansijeservice.mapper.TransactionDTOMapper;
import org.example.finansijeservice.model.PaymentMethod;
import org.example.finansijeservice.model.Transaction;
import org.example.finansijeservice.repository.PaymentMethodsRepository;
import org.example.finansijeservice.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final TransactionDTOMapper mapper;
    private final PaymentMethodsRepository paymentMethodsRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;

    public List<TransactionDTO> getAllTransactions() {
        return mapper.toTransactionDTOList(transactionsRepository.findAll());
    }

    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return mapper.toTransactionDTO(transaction);
    }

    public TransactionDTO createTransaction(TransactionDTO request) {
        PaymentMethod paymentMethod = paymentMethodsRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment method not found with id: " + request.getPaymentMethodId()));

        PaymentProcessor processor = paymentProcessorFactory.create(paymentMethod);
        Transaction tx = mapper.toEntity(request);
        tx.setPaymentMethod(paymentMethod);
        processor.process(tx);

        return mapper.toTransactionDTO(transactionsRepository.save(tx));
    }

    public List<TransactionDTO> getTransactionsByInvoiceId(Long invoiceId) {
        return mapper.toTransactionDTOList(transactionsRepository.findByInvoiceId(invoiceId));
    }

    public List<TransactionDTO> getTransactionsByPaymentMethodId(Long paymentMethodId) {
        return mapper.toTransactionDTOList(transactionsRepository.findByPaymentMethodId(paymentMethodId));
    }

    public Double calculateTotalAmountForInvoice(Long invoiceId) {
        List<Transaction> transactions = transactionsRepository.findByInvoiceId(invoiceId);
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transactionsRepository.deleteById(id);
    }
}
