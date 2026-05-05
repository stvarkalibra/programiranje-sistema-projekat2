package com.ecotravel.accommodation.controller;

import com.ecotravel.accommodation.dto.request.UnitRequestDTO;
import com.ecotravel.accommodation.dto.response.PriceCalculationResponseDTO;
import com.ecotravel.accommodation.dto.response.UnitResponseDTO;
import com.ecotravel.accommodation.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Tag(name = "Smjestajne jedinice (Units)", description = "Upravljanje smjestajnim jedinicama")
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    @Operation(summary = "Pribavi sve smjestajne jedinice")
    public ResponseEntity<List<UnitResponseDTO>> getAll() {
        return ResponseEntity.ok(unitService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi smjestajnu jedinicu po ID-u")
    public ResponseEntity<UnitResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getById(id));
    }

    @GetMapping("/filter/type")
    @Operation(summary = "Filtriraj smjestajne jedinice po tipu",
            description = "Dostupni tipovi: TENT, CABIN, TREEHOUSE, GLAMPING, ECO_LODGE")
    public ResponseEntity<List<UnitResponseDTO>> getByType(
            @Parameter(description = "Tip smjestaja", example = "CABIN")
            @RequestParam String unitType) {
        return ResponseEntity.ok(unitService.getByType(unitType));
    }

    @GetMapping("/filter/city")
    @Operation(summary = "Filtriraj smjestajne jedinice po gradu")
    public ResponseEntity<List<UnitResponseDTO>> getByCity(
            @Parameter(description = "Naziv grada", example = "Zabljak")
            @RequestParam String city) {
        return ResponseEntity.ok(unitService.getByCity(city));
    }

    @GetMapping("/filter/max-price")
    @Operation(summary = "Filtriraj smjestajne jedinice po maksimalnoj cijeni po nocenju")
    public ResponseEntity<List<UnitResponseDTO>> getByMaxPrice(
            @Parameter(description = "Maksimalna cijena po nocenju", example = "100.0")
            @RequestParam Double maxPrice) {
        return ResponseEntity.ok(unitService.getByMaxPrice(maxPrice));
    }

    @GetMapping("/{id}/calculate-price")
    @Operation(summary = "Izracunaj ukupnu cijenu za odredjeni period",
            description = "Uzima u obzir sezonski mnozac i extra_cost sadrzaja")
    public ResponseEntity<PriceCalculationResponseDTO> calculatePrice(
            @PathVariable Long id,
            @Parameter(description = "Datum dolaska", example = "2025-07-10")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @Parameter(description = "Datum odlaska", example = "2025-07-17")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return ResponseEntity.ok(unitService.calculatePrice(id, checkIn, checkOut));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novu smjestajnu jedinicu")
    public ResponseEntity<UnitResponseDTO> create(@Valid @RequestBody UnitRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Azuriraj smjestajnu jedinicu")
    public ResponseEntity<UnitResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody UnitRequestDTO dto) {
        return ResponseEntity.ok(unitService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi smjestajnu jedinicu")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        unitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}