package org.example.rezervacijeservice.strategy;

import org.example.rezervacijeservice.model.CancellationPolicy;
import org.springframework.stereotype.Component;

@Component
public class CancellationStrategyResolver {

    public CancellationRefundStrategy resolve(CancellationPolicy policy) {
        int percentage = policy.getRefundPercentage();
        if (percentage >= 100) return new FullRefundStrategy();
        if (percentage <= 0)   return new NoRefundStrategy();
        return new PartialRefundStrategy(percentage);
    }
}
