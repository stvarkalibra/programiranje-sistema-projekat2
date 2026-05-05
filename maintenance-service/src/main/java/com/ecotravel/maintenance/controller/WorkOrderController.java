package com.ecotravel.maintenance.controller;

import com.ecotravel.maintenance.dto.request.WorkOrderRequestDTO;
import com.ecotravel.maintenance.dto.request.WorkOrderStatusUpdateDTO;
import com.ecotravel.maintenance.dto.response.WorkOrderResponseDTO;
import com.ecotravel.maintenance.service.WorkOrderService;
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
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
@Tag(name = "Radni nalozi (Work Orders)", description = "Upravljanje radnim nalozima odrzavanja")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping
    @Operation(summary = "Pribavi sve radne naloge")
    public ResponseEntity<List<WorkOrderResponseDTO>> getAll() {
        return ResponseEntity.ok(workOrderService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pribavi radni nalog po ID-u")
    public ResponseEntity<WorkOrderResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getById(id));
    }

    @GetMapping("/filter/unit/{unitId}")
    @Operation(summary = "Pribavi sve radne naloge za odredjenu smjestajnu jedinicu")
    public ResponseEntity<List<WorkOrderResponseDTO>> getByUnit(@PathVariable Long unitId) {
        return ResponseEntity.ok(workOrderService.getByUnitId(unitId));
    }

    @GetMapping("/filter/status")
    @Operation(summary = "Filtriraj radne naloge po statusu",
            description = "Dozvoljeni statusi: PENDING, IN_PROGRESS, COMPLETED")
    public ResponseEntity<List<WorkOrderResponseDTO>> getByStatus(
            @Parameter(description = "Status radnog naloga", example = "PENDING")
            @RequestParam String status) {
        return ResponseEntity.ok(workOrderService.getByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi radni nalog",
            description = "Novi radni nalog se automatski postavlja u status PENDING")
    public ResponseEntity<WorkOrderResponseDTO> create(@Valid @RequestBody WorkOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.create(dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Promijeni status radnog naloga",
            description = "Status moze ici samo unaprijed: PENDING -> IN_PROGRESS -> COMPLETED. " +
                    "Kada se postavi COMPLETED, automatski se upisuje trenutno vrijeme zavrsetka.")
    public ResponseEntity<WorkOrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderStatusUpdateDTO dto) {
        return ResponseEntity.ok(workOrderService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obrisi radni nalog")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}