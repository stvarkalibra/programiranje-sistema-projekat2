package org.example.rezervacijeservice.strategy;

import org.springframework.stereotype.Component;

@Component
public class NoRefundStrategy implements CancellationRefundStrategy {

    @Override
    public double calculateRefund(double totalPrice) {
        return 0.0;
    }
}
