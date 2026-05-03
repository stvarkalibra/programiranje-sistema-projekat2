package org.example.rezervacijeservice.strategy;

public interface CancellationRefundStrategy {
    double calculateRefund(double totalPrice);
}
