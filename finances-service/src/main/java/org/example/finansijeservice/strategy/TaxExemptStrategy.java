package org.example.finansijeservice.strategy;

import org.springframework.stereotype.Component;

@Component
public class TaxExemptStrategy implements TaxCalculationStrategy {

    @Override
    public double calculate(double subtotal, double taxPercentage) {
        return subtotal;
    }
}
