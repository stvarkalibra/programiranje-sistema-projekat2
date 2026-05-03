package org.example.finansijeservice.factory;

import org.example.finansijeservice.model.Transaction;

public interface PaymentProcessor {
    void process(Transaction transaction);
    String getSupportedMethod();
}
