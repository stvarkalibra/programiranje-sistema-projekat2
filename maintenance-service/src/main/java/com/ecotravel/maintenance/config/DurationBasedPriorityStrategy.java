package com.ecotravel.maintenance.config;

import org.springframework.stereotype.Component;

/**
 * prioritet na osnovu trajanja zadatka, kraci zadaci dobijaju visi prioritet, brze se zavrsavaju.
 */
@Component("durationPriorityStrategy")
public class DurationBasedPriorityStrategy implements MaintenancePriorityStrategy {

    @Override
    public int calculatePriorityScore(String unitType, Double basePricePerNight, Integer estimatedDurationHours) {
        int score = 50; // pocetni score

        if (estimatedDurationHours != null) {
            if (estimatedDurationHours <= 2) score += 30;
            else if (estimatedDurationHours <= 4) score += 15;
            else score -= 10;
        }

        // satori imaju visi prioritet jer ima vise njih
        score += switch (unitType.toUpperCase()) {
            case "TENT" -> 20;
            case "CABIN" -> 15;
            case "TREEHOUSE" -> 10;
            default -> 5;
        };

        return Math.min(Math.max(score, 0), 100);
    }

    @Override
    public String getStrategyName() {
        return "DURATION_BASED";
    }
}