package com.ecotravel.maintenance.controller;

import com.ecotravel.maintenance.dto.request.WorkerRequestDTO;
import com.ecotravel.maintenance.dto.response.WorkerResponseDTO;
import com.ecotravel.maintenance.dto.response.WorkerSummaryResponseDTO;
import com.ecotravel.maintenance.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@Tag(name = "Radnici (Workers)", description = "Upravljanje radnicima odrzavanja")
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    @Operation(summary = "Pribavi sve radnike")
    public ResponseEntity<List<WorkerResponseDTO>> getAll() {
        return ResponseEntity.ok(workerService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi radnika po ID-u")
    public ResponseEntity<WorkerResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.getById(id));
    }

    @GetMapping("/filter/specialty")
    @Operation(summary = "Filtriraj radnike po specijalnosti",
            description = "Dostupne specijalnosti: CLEANER, ELECTRICIAN, PLUMBER, HVAC, HANDYMAN, GARDENER")
    public ResponseEntity<List<WorkerResponseDTO>> getBySpecialty(
            @Parameter(description = "Specijalnost radnika", example = "ELECTRICIAN")
            @RequestParam String specialty) {
        return ResponseEntity.ok(workerService.getBySpecialty(specialty));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Pribavi sumarni prikaz opterecenja radnika",
            description = "Vraca broj naloga po statusu i ukupne procijenjene sate aktivnih naloga")
    public ResponseEntity<WorkerSummaryResponseDTO> getWorkerSummary(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.getWorkerSummary(id));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novog radnika")
    public ResponseEntity<WorkerResponseDTO> create(@Valid @RequestBody WorkerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workerService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Azuriraj podatke o radniku")
    public ResponseEntity<WorkerResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody WorkerRequestDTO dto) {
        return ResponseEntity.ok(workerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi radnika")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}