package org.example.finansijeservice.factory;

import lombok.RequiredArgsConstructor;
import org.example.finansijeservice.model.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {

    private final List<PaymentProcessor> processors;

    public PaymentProcessor create(PaymentMethod paymentMethod) {
        return processors.stream()
                .filter(p -> p.getSupportedMethod().equalsIgnoreCase(paymentMethod.getMethod_name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No processor found for payment method: " + paymentMethod.getMethod_name()));
    }
}
