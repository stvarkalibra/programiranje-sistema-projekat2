package org.example.rezervacijeservice.strategy;

public class PartialRefundStrategy implements CancellationRefundStrategy {

    private final int refundPercentage;

    public PartialRefundStrategy(int refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    @Override
    public double calculateRefund(double totalPrice) {
        return totalPrice * (refundPercentage / 100.0);
    }
}
