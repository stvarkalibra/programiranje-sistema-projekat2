package org.example.finansijeservice.factory;

import org.example.finansijeservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class BankTransferProcessor implements PaymentProcessor {

    @Override
    public void process(Transaction transaction) {
        System.out.println("Processing bank transfer of: " + transaction.getAmount());
    }

    @Override
    public String getSupportedMethod() {
        return "BANK_TRANSFER";
    }
}
