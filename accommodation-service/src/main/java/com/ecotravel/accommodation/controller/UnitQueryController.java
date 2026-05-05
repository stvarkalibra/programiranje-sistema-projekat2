package com.ecotravel.accommodation.controller;

import com.ecotravel.accommodation.dto.response.UnitSummaryResponseDTO;
import com.ecotravel.accommodation.service.UnitQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/units/query")
@RequiredArgsConstructor
@Tag(name = "Upiti o smjestajnim jedinicama", description = "Endpointi za medjuservisnu komunikaciju i slozene upite")
public class UnitQueryController {

    private final UnitQueryService unitQueryService;

    @GetMapping("/{id}/summary")
    @Operation(summary = "Dohvati sumarni prikaz smjestajne jedinice",
            description = "Koristi maintenance servis za validaciju radnog naloga")
    public ResponseEntity<UnitSummaryResponseDTO> getUnitSummary(@PathVariable Long id) {
        return ResponseEntity.ok(unitQueryService.getUnitSummary(id));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Provjeri dostupnost jedinice za odrzavanje na odredjeni datum")
    public ResponseEntity<UnitSummaryResponseDTO> getUnitAvailability(
            @PathVariable Long id,
            @Parameter(description = "Datum planiranog odrzavanja", example = "2025-06-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(unitQueryService.getUnitAvailabilityStatus(id, date));
    }

    @GetMapping("/by-city")
    @Operation(summary = "Dohvati sve jedinice sa sadrzajima za odredjeni grad",
            description = "Koristi JOIN FETCH - izbjegava N+1 problem")
    public ResponseEntity<List<UnitSummaryResponseDTO>> getByCity(
            @Parameter(description = "Naziv grada", example = "Zabljak")
            @RequestParam String city) {
        return ResponseEntity.ok(unitQueryService.getUnitsWithFacilitiesByCity(city));
    }

    @GetMapping("/by-type-and-price")
    @Operation(summary = "Dohvati jedinice po tipu i maksimalnoj ukupnoj cijeni",
            description = "Agregacijski upit koji racuna ukupnu cijenu sa sadrzajima")
    public ResponseEntity<List<UnitSummaryResponseDTO>> getByTypeAndMaxPrice(
            @RequestParam String unitType,
            @RequestParam Double maxTotalPrice) {
        return ResponseEntity.ok(unitQueryService.getUnitsByTypeAndMaxTotalPrice(unitType, maxTotalPrice));
    }

    @GetMapping("/by-facility")
    @Operation(summary = "Dohvati jedinice koje imaju odredjeni sadrzaj")
    public ResponseEntity<List<UnitSummaryResponseDTO>> getByFacility(
            @Parameter(description = "Naziv sadrzaja", example = "Privatni bazen")
            @RequestParam String facilityName) {
        return ResponseEntity.ok(unitQueryService.getUnitsByFacility(facilityName));
    }
}