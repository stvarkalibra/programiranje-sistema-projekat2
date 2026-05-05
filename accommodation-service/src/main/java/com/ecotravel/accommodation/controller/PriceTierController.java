package com.ecotravel.accommodation.controller;

import com.ecotravel.accommodation.dto.request.PriceTierRequestDTO;
import com.ecotravel.accommodation.dto.response.PriceTierResponseDTO;
import com.ecotravel.accommodation.service.PriceTierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-tiers")
@RequiredArgsConstructor
@Tag(name = "Sezonski cjenovnik (Price Tiers)", description = "Upravljanje sezonskim cijenama")
public class PriceTierController {

    private final PriceTierService priceTierService;

    @GetMapping
    @Operation(summary = "Pribavi sve sezonske cjenovnike")
    public ResponseEntity<List<PriceTierResponseDTO>> getAll() {
        return ResponseEntity.ok(priceTierService.getAll());
    }

    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Pribavi sve cjenovnike za odredjenu smjestajnu jedinicu")
    public ResponseEntity<List<PriceTierResponseDTO>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(priceTierService.getByUnitId(unitId));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi sezonski cjenovnik")
    public ResponseEntity<PriceTierResponseDTO> create(@Valid @RequestBody PriceTierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(priceTierService.create(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi sezonski cjenovnik")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceTierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}