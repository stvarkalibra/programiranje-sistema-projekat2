package com.ecotravel.maintenance.service;

import com.ecotravel.maintenance.client.AccommodationClient;
import com.ecotravel.maintenance.config.MaintenanceConfigSingleton;
import com.ecotravel.maintenance.config.MaintenancePriorityStrategy;
import com.ecotravel.maintenance.dto.external.AccommodationUnitDTO;
import com.ecotravel.maintenance.dto.response.WorkOrderResponseDTO;
import com.ecotravel.maintenance.model.WorkOrder;
import com.ecotravel.maintenance.repository.WorkOrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationIntegrationService {

    private final AccommodationClient accommodationClient;
    private final WorkOrderRepository workOrderRepository;
    private final Map<String, MaintenancePriorityStrategy> priorityStrategies;

    // FEIGN POZIVI SA CIRCUIT BREAKER + RETRY + FALLBACK

    @CircuitBreaker(name = "accommodationService", fallbackMethod = "fallbackGetUnitSummary")
    @Retry(name = "accommodationService")
    public AccommodationUnitDTO getUnitSummary(Long unitId) {
        log.info("Pozivam accommodation servis za jedinicu ID: {}", unitId);
        return accommodationClient.getUnitSummary(unitId);
    }

    //dostupnost jedinica za odrzavanje
    @CircuitBreaker(name = "accommodationService", fallbackMethod = "fallbackGetUnitAvailability")
    @Retry(name = "accommodationService")
    public AccommodationUnitDTO getUnitAvailability(Long unitId, LocalDate date) {
        log.info("Provjeravam dostupnost jedinice {} za datum {}", unitId, date);
        return accommodationClient.getUnitAvailability(unitId, date);
    }

    // sve jedinice u gradu
    @CircuitBreaker(name = "accommodationService", fallbackMethod = "fallbackGetUnitsByCity")
    @Retry(name = "accommodationService")
    public List<AccommodationUnitDTO> getUnitsByCity(String city) {
        log.info("Dohvatam jedinice za grad: {}", city);
        return accommodationClient.getUnitsByCity(city);
    }

    // jedinice po tipu i cijeni
    @CircuitBreaker(name = "accommodationService", fallbackMethod = "fallbackGetUnitsByTypeAndPrice")
    @Retry(name = "accommodationService")
    public List<AccommodationUnitDTO> getUnitsByTypeAndPrice(String unitType, Double maxPrice) {
        log.info("Dohvatam jedinice tipa {} sa max cijenom {}", unitType, maxPrice);
        return accommodationClient.getUnitsByTypeAndMaxPrice(unitType, maxPrice);
    }


    // FALLBACK METODE

    public AccommodationUnitDTO fallbackGetUnitSummary(Long unitId, Exception ex) {
        log.warn("FALLBACK: Accommodation servis nedostupan za jedinicu {}. Greska: {}", unitId, ex.getMessage());
        AccommodationUnitDTO fallback = new AccommodationUnitDTO();
        fallback.setId(unitId);
        fallback.setName("Informacije o smjestajnoj jedinici trenutno nedostupne");
        fallback.setIsAvailable(null);
        return fallback;
    }

    public AccommodationUnitDTO fallbackGetUnitAvailability(Long unitId, LocalDate date, Exception ex) {
        log.warn("FALLBACK: Ne mogu provjeriti dostupnost jedinice {} za {}. Greska: {}", unitId, date, ex.getMessage());
        AccommodationUnitDTO fallback = new AccommodationUnitDTO();
        fallback.setId(unitId);
        fallback.setName("Provjera dostupnosti trenutno nije moguca");
        fallback.setIsAvailable(null);
        return fallback;
    }

    public List<AccommodationUnitDTO> fallbackGetUnitsByCity(String city, Exception ex) {
        log.warn("FALLBACK: Ne mogu dohvatiti jedinice za grad {}. Greska: {}", city, ex.getMessage());
        return List.of();
    }

    public List<AccommodationUnitDTO> fallbackGetUnitsByTypeAndPrice(String unitType, Double maxPrice, Exception ex) {
        log.warn("FALLBACK: Ne mogu dohvatiti jedinice tipa {}. Greska: {}", unitType, ex.getMessage());
        return List.of();
    }



    /**
     Validacija radnog naloga prije kreiranja.
     */
    public void validateWorkOrderCreation(Long unitId, Long workerId, LocalDate scheduledDate) {
        MaintenanceConfigSingleton config = MaintenanceConfigSingleton.getInstance();

        // Provjera 1: Postoji li smjestajna jedinica
        AccommodationUnitDTO unit = getUnitSummary(unitId);
        if (unit.getName().contains("nedostupne")) {
            log.warn("Accommodation servis nedostupan, preskacemo provjeru jedinice {}", unitId);
            // Ne bacamo gresku - fallback je aktivan, dozvoljavamo kreiranje
            return;
        }

        // Provjera 2: Da li je jedinica slobodna za odrzavanje (ako je ukljucena striktna provjera)
        if (config.isStrictAvailabilityCheck()) {
            AccommodationUnitDTO availability = getUnitAvailability(unitId, scheduledDate);
            if (availability.getIsAvailable() != null && !availability.getIsAvailable()) {
                throw new IllegalStateException(
                        "Smjestajna jedinica " + unitId + " je zauzeta na datum " + scheduledDate +
                                ". Ne moze se planirati odrzavanje dok je gost prisutan."
                );
            }
        }

        // Provjera 3: Da li radnik ima previse aktivnih naloga
        long activeOrders = workOrderRepository.findByWorker_Id(workerId)
                .stream()
                .filter(o -> "PENDING".equals(o.getStatus()) || "IN_PROGRESS".equals(o.getStatus()))
                .count();

        if (activeOrders >= config.getMaxActiveOrdersPerWorker()) {
            throw new IllegalStateException(
                    "Radnik " + workerId + " vec ima " + activeOrders + " aktivnih naloga. " +
                            "Maksimum je " + config.getMaxActiveOrdersPerWorker() + "."
            );
        }
    }

    /**
    PROVJERA 2: Izracunaj prioritet radnog naloga koristeci aktivnu strategiju.
     */
    public int calculateWorkOrderPriority(Long unitId, Integer estimatedHours) {
        AccommodationUnitDTO unit = getUnitSummary(unitId);

        if (unit.getUnitType() == null) {
            log.warn("Tip jedinice nedostupan, koristimo defaultni prioritet");
            return 50;
        }

        MaintenanceConfigSingleton config = MaintenanceConfigSingleton.getInstance();
        String strategyName = config.getActivePriorityStrategy().toLowerCase()
                .replace("_", "") + "PriorityStrategy";

        // Koristi aktivnu strategiju iz Strategy patterna
        MaintenancePriorityStrategy strategy = priorityStrategies.getOrDefault(
                config.getActivePriorityStrategy().equals("PRICE_BASED")
                        ? "pricePriorityStrategy" : "durationPriorityStrategy",
                priorityStrategies.get("pricePriorityStrategy")
        );

        int score = strategy.calculatePriorityScore(
                unit.getUnitType(),
                unit.getBasePricePerNight(),
                estimatedHours
        );

        log.info("Prioritet radnog naloga za jedinicu {} (tip: {}): {} bodova (strategija: {})",
                unitId, unit.getUnitType(), score, strategy.getStrategyName());

        return score;
    }

    /**
    PROVJERA 3: Provjeri opterecenje po gradu - da li ima previse naloga u jednom gradu.
     */
    public String analyzeMaintenanceLoadByCity(String city) {
        // Dohvati sve jedinice u gradu
        List<AccommodationUnitDTO> unitsInCity = getUnitsByCity(city);

        if (unitsInCity.isEmpty()) {
            return "Nema podataka o smjestajnim jedinicama u gradu " + city +
                    " (accommodation servis mozda nedostupan)";
        }

        // Dohvati lokalne ID-jeve jedinica u tom gradu
        List<Long> unitIds = unitsInCity.stream()
                .map(AccommodationUnitDTO::getId)
                .collect(Collectors.toList());

        // Prebroji aktivne naloge za te jedinice
        long pendingOrders = workOrderRepository.findAll().stream()
                .filter(o -> unitIds.contains(o.getUnitId()))
                .filter(o -> "PENDING".equals(o.getStatus()) || "IN_PROGRESS".equals(o.getStatus()))
                .count();

        double loadPercentage = unitsInCity.size() > 0
                ? (double) pendingOrders / unitsInCity.size() * 100 : 0;

        return String.format(
                "Grad: %s | Jedinice: %d | Aktivni nalozi: %d | Opterecenje: %.1f%%",
                city, unitsInCity.size(), pendingOrders, loadPercentage
        );
    }

    /**
    Dohvati radne naloge sortirane po prioritetu (skupi objekti prvi).
     */
    public List<WorkOrder> getWorkOrdersSortedByPriority() {
        return workOrderRepository.findAll().stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .sorted((o1, o2) -> {
                    int p1 = calculateWorkOrderPriority(o1.getUnitId(),
                            o1.getTask().getEstimatedDurationHours());
                    int p2 = calculateWorkOrderPriority(o2.getUnitId(),
                            o2.getTask().getEstimatedDurationHours());
                    return Integer.compare(p2, p1); // descending
                })
                .collect(Collectors.toList());
    }
}