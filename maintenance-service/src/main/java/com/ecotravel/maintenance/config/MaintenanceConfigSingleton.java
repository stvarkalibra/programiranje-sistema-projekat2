package com.ecotravel.maintenance.config;

/**
 * singleton (Grupa A)
 */
public class MaintenanceConfigSingleton {

    private static volatile MaintenanceConfigSingleton instance;

    // Konfigurabilni parametri
    private String activePriorityStrategy = "PRICE_BASED";
    private int maxActiveOrdersPerWorker = 3;
    private int maxEstimatedHoursPerDay = 8;
    private boolean strictAvailabilityCheck = true;

    private MaintenanceConfigSingleton() {}

   // thread-safe Singleton getInstance sa double-checked locking.

    public static MaintenanceConfigSingleton getInstance() {
        if (instance == null) {
            synchronized (MaintenanceConfigSingleton.class) {
                if (instance == null) {
                    instance = new MaintenanceConfigSingleton();
                }
            }
        }
        return instance;
    }

    public String getActivePriorityStrategy() {
        return activePriorityStrategy;
    }

    public void setActivePriorityStrategy(String strategy) {
        this.activePriorityStrategy = strategy;
    }

    public int getMaxActiveOrdersPerWorker() {
        return maxActiveOrdersPerWorker;
    }

    public void setMaxActiveOrdersPerWorker(int max) {
        this.maxActiveOrdersPerWorker = max;
    }

    public int getMaxEstimatedHoursPerDay() {
        return maxEstimatedHoursPerDay;
    }

    public boolean isStrictAvailabilityCheck() {
        return strictAvailabilityCheck;
    }

    public void setStrictAvailabilityCheck(boolean strict) {
        this.strictAvailabilityCheck = strict;
    }

    @Override
    public String toString() {
        return "MaintenanceConfig{" +
                "strategy='" + activePriorityStrategy + '\'' +
                ", maxOrdersPerWorker=" + maxActiveOrdersPerWorker +
                ", maxHoursPerDay=" + maxEstimatedHoursPerDay +
                ", strictAvailability=" + strictAvailabilityCheck +
                '}';
    }
}