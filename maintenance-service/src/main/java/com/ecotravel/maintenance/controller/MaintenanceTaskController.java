package com.ecotravel.maintenance.controller;

import com.ecotravel.maintenance.dto.request.MaintenanceTaskRequestDTO;
import com.ecotravel.maintenance.dto.response.MaintenanceTaskResponseDTO;
import com.ecotravel.maintenance.service.MaintenanceTaskService;
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
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tipovi zadataka (Maintenance Tasks)", description = "Upravljanje tipovima zadataka odrzavanja")
public class MaintenanceTaskController {

    private final MaintenanceTaskService maintenanceTaskService;

    @GetMapping
    @Operation(summary = "Pribavi sve tipove zadataka")
    public ResponseEntity<List<MaintenanceTaskResponseDTO>> getAll() {
        return ResponseEntity.ok(maintenanceTaskService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi tip zadatka po ID-u")
    public ResponseEntity<MaintenanceTaskResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceTaskService.getById(id));
    }

    @GetMapping("/filter/max-duration")
    @Operation(summary = "Filtriraj zadatke po maksimalnom trajanju",
            description = "Vraca sve zadatke cijeje procijenjeno trajanje manje ili jednako zadanom broju sati")
    public ResponseEntity<List<MaintenanceTaskResponseDTO>> getByMaxDuration(
            @Parameter(description = "Maksimalan broj sati trajanja", example = "4")
            @RequestParam Integer maxHours) {
        return ResponseEntity.ok(maintenanceTaskService.getByMaxDuration(maxHours));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi tip zadatka")
    public ResponseEntity<MaintenanceTaskResponseDTO> create(@Valid @RequestBody MaintenanceTaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceTaskService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Azuriraj tip zadatka")
    public ResponseEntity<MaintenanceTaskResponseDTO> update(@PathVariable Long id,
                                                             @Valid @RequestBody MaintenanceTaskRequestDTO dto) {
        return ResponseEntity.ok(maintenanceTaskService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi tip zadatka")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}