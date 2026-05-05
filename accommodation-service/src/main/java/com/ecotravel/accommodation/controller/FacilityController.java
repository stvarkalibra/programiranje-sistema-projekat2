package com.ecotravel.accommodation.controller;

import com.ecotravel.accommodation.dto.request.FacilityRequestDTO;
import com.ecotravel.accommodation.dto.response.FacilityResponseDTO;
import com.ecotravel.accommodation.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
@Tag(name = "Sadrzaji (Facilities)", description = "Upravljanje sadrzajima smjestajnih jedinica")
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    @Operation(summary = "Pribavi sve sadrzaje")
    public ResponseEntity<List<FacilityResponseDTO>> getAll() {
        return ResponseEntity.ok(facilityService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi sadrzaj po ID-u")
    public ResponseEntity<FacilityResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi sadrzaj")
    public ResponseEntity<FacilityResponseDTO> create(@Valid @RequestBody FacilityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Azuriraj sadrzaj")
    public ResponseEntity<FacilityResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody FacilityRequestDTO dto) {
        return ResponseEntity.ok(facilityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi sadrzaj")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}