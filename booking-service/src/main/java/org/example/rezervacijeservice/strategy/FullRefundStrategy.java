package org.example.rezervacijeservice.strategy;

import org.springframework.stereotype.Component;

@Component
public class FullRefundStrategy implements CancellationRefundStrategy {

    @Override
    public double calculateRefund(double totalPrice) {
        return totalPrice;
    }
}
