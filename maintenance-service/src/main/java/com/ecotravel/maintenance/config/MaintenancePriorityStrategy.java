package com.ecotravel.maintenance.config;

/**
 * dizajn obrazac: Strategy (Grupa B)
 */
public interface MaintenancePriorityStrategy {

    /**
     * Racunanje prioritetnog skora za odrzavanje, visi skor je visi prioritet
     *
     * @param unitType tip smjestajne jedinice (CABIN, TENT, GLAMPING...)
     * @param basePricePerNight osnovna cijena po nocenju
     * @param estimatedDurationHours procijenjeno trajanje zadatka
     * @return prioritetni skor (0-100)
     */
    int calculatePriorityScore(String unitType, Double basePricePerNight, Integer estimatedDurationHours);

    String getStrategyName();
}