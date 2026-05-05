package com.ecotravel.maintenance.controller;

import com.ecotravel.maintenance.dto.request.MaintenanceLogRequestDTO;
import com.ecotravel.maintenance.dto.response.MaintenanceLogResponseDTO;
import com.ecotravel.maintenance.service.MaintenanceLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-logs")
@RequiredArgsConstructor
@Tag(name = "Historija odrzavanja (Maintenance Logs)", description = "Pregled i upis historije odrzavanja smjestajnih jedinica")
public class MaintenanceLogController {

    private final MaintenanceLogService maintenanceLogService;

    @GetMapping
    @Operation(summary = "Pribavi svu historiju odrzavanja")
    public ResponseEntity<List<MaintenanceLogResponseDTO>> getAll() {
        return ResponseEntity.ok(maintenanceLogService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi zapis historije po ID-u")
    public ResponseEntity<MaintenanceLogResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceLogService.getById(id));
    }

    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Pribavi historiju odrzavanja za odredjenu smjestajnu jedinicu")
    public ResponseEntity<List<MaintenanceLogResponseDTO>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(maintenanceLogService.getByUnitId(unitId));
    }

    @PostMapping
    @Operation(summary = "Dodaj novi zapis u historiju odrzavanja")
    public ResponseEntity<MaintenanceLogResponseDTO> create(@Valid @RequestBody MaintenanceLogRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceLogService.create(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi zapis iz historije odrzavanja")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}