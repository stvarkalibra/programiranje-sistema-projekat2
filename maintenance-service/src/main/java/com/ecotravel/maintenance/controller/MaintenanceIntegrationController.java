package com.ecotravel.maintenance.controller;

import com.ecotravel.maintenance.config.MaintenanceConfigSingleton;
import com.ecotravel.maintenance.dto.external.AccommodationUnitDTO;
import com.ecotravel.maintenance.service.AccommodationIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance/integration")
@RequiredArgsConstructor
@Tag(name = "Integracija sa Accommodation servisom",
        description = "Endpointi koji pozivaju accommodation servis (Feign + Circuit Breaker + Retry)")
public class MaintenanceIntegrationController {

    private final AccommodationIntegrationService integrationService;

    @GetMapping("/unit/{id}")
    @Operation(summary = "Dohvati podatke o smjestajnoj jedinici",
            description = "Poziva accommodation servis. Ima Retry (3x) i Circuit Breaker. " +
                    "Ako servis pada, vraca fallback odgovor.")
    public ResponseEntity<AccommodationUnitDTO> getUnitDetails(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.getUnitSummary(id));
    }

    @GetMapping("/unit/{id}/availability")
    @Operation(summary = "Provjeri dostupnost jedinice za odrzavanje",
            description = "Vraca true ako nema booking-a na taj datum (slobodna za odrzavanje)")
    public ResponseEntity<AccommodationUnitDTO> checkUnitAvailability(
            @PathVariable Long id,
            @Parameter(description = "Datum planiranog odrzavanja", example = "2025-06-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(integrationService.getUnitAvailability(id, date));
    }

    @GetMapping("/units/by-city")
    @Operation(summary = "Dohvati sve smjestajne jedinice u gradu",
            description = "Koristi se za planiranje odrzavanja po lokaciji")
    public ResponseEntity<List<AccommodationUnitDTO>> getUnitsByCity(
            @Parameter(description = "Naziv grada", example = "Zabljak")
            @RequestParam String city) {
        return ResponseEntity.ok(integrationService.getUnitsByCity(city));
    }

    @GetMapping("/units/by-type-and-price")
    @Operation(summary = "Dohvati jedinice po tipu i maksimalnoj cijeni")
    public ResponseEntity<List<AccommodationUnitDTO>> getUnitsByTypeAndPrice(
            @RequestParam String unitType,
            @RequestParam Double maxTotalPrice) {
        return ResponseEntity.ok(integrationService.getUnitsByTypeAndPrice(unitType, maxTotalPrice));
    }

    @GetMapping("/unit/{id}/priority")
    @Operation(summary = "Izracunaj prioritet odrzavanja za smjestajnu jedinicu",
            description = "Koristi Strategy pattern i Singleton konfiguraciju za kalkulaciju prioriteta")
    public ResponseEntity<Map<String, Object>> calculatePriority(
            @PathVariable Long id,
            @RequestParam Integer estimatedHours) {
        int score = integrationService.calculateWorkOrderPriority(id, estimatedHours);
        String activeStrategy = MaintenanceConfigSingleton.getInstance().getActivePriorityStrategy();
        return ResponseEntity.ok(Map.of(
                "unitId", id,
                "priorityScore", score,
                "activeStrategy", activeStrategy,
                "interpretation", score >= 70 ? "VISOKI PRIORITET" :
                        score >= 40 ? "SREDNJI PRIORITET" : "NISKI PRIORITET"
        ));
    }

    @GetMapping("/load/city")
    @Operation(summary = "Analiziraj opterecenje odrzavanja po gradu",
            description = "Kombinuje podatke iz accommodation servisa i lokalne baze")
    public ResponseEntity<Map<String, String>> analyzeLoadByCity(
            @Parameter(description = "Naziv grada", example = "Zabljak")
            @RequestParam String city) {
        String analysis = integrationService.analyzeMaintenanceLoadByCity(city);
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }

    @GetMapping("/config")
    @Operation(summary = "Prikazi aktivnu konfiguraciju maintenance servisa (Singleton)",
            description = "Prikazuje trenutno aktivnu strategiju prioritizacije i ostale parametre")
    public ResponseEntity<Map<String, Object>> getConfig() {
        MaintenanceConfigSingleton config = MaintenanceConfigSingleton.getInstance();
        return ResponseEntity.ok(Map.of(
                "activePriorityStrategy", config.getActivePriorityStrategy(),
                "maxActiveOrdersPerWorker", config.getMaxActiveOrdersPerWorker(),
                "maxEstimatedHoursPerDay", config.getMaxEstimatedHoursPerDay(),
                "strictAvailabilityCheck", config.isStrictAvailabilityCheck()
        ));
    }

    @PatchMapping("/config/strategy")
    @Operation(summary = "Promijeni aktivnu strategiju prioritizacije (Strategy pattern)",
            description = "Dostupne strategije: PRICE_BASED, DURATION_BASED")
    public ResponseEntity<Map<String, String>> changeStrategy(
            @Parameter(description = "Naziv strategije", example = "DURATION_BASED")
            @RequestParam String strategy) {
        if (!List.of("PRICE_BASED", "DURATION_BASED").contains(strategy.toUpperCase())) {
            return ResponseEntity.badRequest().body(
                    Map.of("greska", "Nepoznata strategija. Dostupne: PRICE_BASED, DURATION_BASED"));
        }
        MaintenanceConfigSingleton.getInstance().setActivePriorityStrategy(strategy.toUpperCase());
        return ResponseEntity.ok(Map.of(
                "poruka", "Strategija promijenjena na: " + strategy.toUpperCase()));
    }
}