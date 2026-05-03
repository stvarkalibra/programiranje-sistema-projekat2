package org.example.finansijeservice.strategy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StandardTaxStrategy implements TaxCalculationStrategy {

    @Override
    public double calculate(double subtotal, double taxPercentage) {
        return subtotal * (1 + taxPercentage / 100);
    }
}
