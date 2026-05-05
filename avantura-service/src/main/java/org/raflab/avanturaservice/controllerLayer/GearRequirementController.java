package org.raflab.avanturaservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.GearRequirementRequest;
import org.raflab.avanturaservice.dtoLayer.response.GearRequirementResponse;
import org.raflab.avanturaservice.serviceLayer.GearRequirementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gear")
@RequiredArgsConstructor
@Tag(name = "Gear Requirement Controller", description = "Upravljanje potrebnom opremom")
public class GearRequirementController {

    private final GearRequirementService gearService;

    @Operation(summary = "Prikaz sve opreme")
    @GetMapping
    public ResponseEntity<List<GearRequirementResponse>> getAll() {
        return ResponseEntity.ok(gearService.getAllGearRequirements());
    }

    @Operation(summary = "Prikaz opreme po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<GearRequirementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gearService.getGearRequirementById(id));
    }

    @Operation(summary = "Dodavanje nove opreme")
    @PostMapping
    public ResponseEntity<GearRequirementResponse> create(@Valid @RequestBody GearRequirementRequest request) {
        return new ResponseEntity<>(gearService.createGearRequirement(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena stavke opreme")
    @PutMapping("/{id}")
    public ResponseEntity<GearRequirementResponse> update(@PathVariable Long id, @Valid @RequestBody GearRequirementRequest request) {
        return ResponseEntity.ok(gearService.updateGearRequirement(id, request));
    }

    @Operation(summary = "Brisanje stavke opreme")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gearService.deleteGearRequirement(id);
        return ResponseEntity.noContent().build();
    }

    // Dodaj u GearRequirementController
    @Operation(summary = "Proveri bezbednost opreme naspram težine [LOGIKA]")
    @GetMapping("/safety-check/{adventureId}")
    public ResponseEntity<String> checkSafety(@PathVariable Long adventureId) {
        return ResponseEntity.ok(gearService.checkSafetyGearCompliance(adventureId));
    }

    @Operation(summary = "Izbroj obavezne stavke opreme [MATEMATIKA]")
    @GetMapping("/count-mandatory/{adventureId}")
    public ResponseEntity<Long> countMandatory(@PathVariable Long adventureId) {
        return ResponseEntity.ok(gearService.countMandatoryGear(adventureId));
    }
}