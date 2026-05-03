package org.example.finansijeservice.strategy;

public interface TaxCalculationStrategy {
    double calculate(double subtotal, double taxPercentage);
}
