package com.ecotravel.maintenance.config;

import org.springframework.stereotype.Component;

/**
 * konkretna strategija: Prioritet na osnovu cijene objekta, skuplji objekti veci prioritet
 */
@Component("pricePriorityStrategy")
public class PriceBasedPriorityStrategy implements MaintenancePriorityStrategy {

    @Override
    public int calculatePriorityScore(String unitType, Double basePricePerNight, Integer estimatedDurationHours) {
        int score = 0;

        // na osnovu tipa objekta
        score += switch (unitType.toUpperCase()) {
            case "GLAMPING" -> 40;
            case "ECO_LODGE" -> 35;
            case "TREEHOUSE" -> 30;
            case "CABIN" -> 20;
            case "TENT" -> 10;
            default -> 15;
        };

        // na osnovu cijene (max 40 bodova)
        if (basePricePerNight != null) {
            if (basePricePerNight >= 100) score += 40;
            else if (basePricePerNight >= 75) score += 30;
            else if (basePricePerNight >= 50) score += 20;
            else score += 10;
        }

        // na osnovu trajanja zadatka (max 20 bodova)
        // duzi zadaci su hitniji
        if (estimatedDurationHours != null) {
            if (estimatedDurationHours >= 5) score += 20;
            else if (estimatedDurationHours >= 3) score += 15;
            else score += 10;
        }

        return Math.min(score, 100);
    }

    @Override
    public String getStrategyName() {
        return "PRICE_BASED";
    }
}