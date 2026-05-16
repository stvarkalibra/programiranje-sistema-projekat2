package org.example.finansijeservice.factory;

import org.example.finansijeservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class CreditCardProcessor implements PaymentProcessor {

    @Override
    public void process(Transaction transaction) {
        System.out.println("Processing credit card payment of: " + transaction.getAmount());
    }

    @Override
    public String getSupportedMethod() {
        return "Kreditna kartica";
    }
}
