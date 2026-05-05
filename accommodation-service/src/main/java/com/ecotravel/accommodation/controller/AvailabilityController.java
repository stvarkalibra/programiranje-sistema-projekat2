package com.ecotravel.accommodation.controller;

import com.ecotravel.accommodation.dto.request.AvailabilityRequestDTO;
import com.ecotravel.accommodation.dto.response.AvailabilityResponseDTO;
import com.ecotravel.accommodation.service.AvailabilityService;
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
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(name = "Kalendar dostupnosti (Availability)", description = "Upravljanje dostupnoscu smjestajnih jedinica")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Pribavi sve blokirane periode za smjestajnu jedinicu")
    public ResponseEntity<List<AvailabilityResponseDTO>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(availabilityService.getByUnitId(unitId));
    }

    @GetMapping("/check")
    @Operation(summary = "Provjeri da li je smjestajna jedinica slobodna u odabranom periodu",
            description = "Vraca true ako je slobodna, false ako je zauzeta")
    public ResponseEntity<Boolean> checkAvailability(
            @Parameter(description = "ID smjestajne jedinice", example = "1")
            @RequestParam Long unitId,
            @Parameter(description = "Datum dolaska", example = "2025-08-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Datum odlaska", example = "2025-08-07")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(availabilityService.checkAvailability(unitId, startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "Blokiraj periode u kalendaru dostupnosti")
    public ResponseEntity<AvailabilityResponseDTO> blockDates(@Valid @RequestBody AvailabilityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityService.blockDates(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi zapis iz kalendara dostupnosti (odblokiranje)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        availabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}