package org.example.finansijeservice.strategy;

import org.springframework.stereotype.Component;

@Component
public class CompoundTaxStrategy implements TaxCalculationStrategy {

    @Override
    public double calculate(double subtotal, double taxPercentage) {
        double factor = 1 + taxPercentage / 100;
        return subtotal * factor * factor;
    }
}
