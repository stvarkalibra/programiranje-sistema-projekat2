package org.raflab.avanturaservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.TimeSlotRequest;
import org.raflab.avanturaservice.dtoLayer.response.TimeSlotResponse;
import org.raflab.avanturaservice.serviceLayer.TimeSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
@Tag(name = "TimeSlot Controller", description = "Upravljanje terminima za avanture")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getAll() {
        return ResponseEntity.ok(timeSlotService.getAllTimeSlots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(timeSlotService.getTimeSlotById(id));
    }

    @PostMapping
    public ResponseEntity<TimeSlotResponse> create(@Valid @RequestBody TimeSlotRequest request) {
        return new ResponseEntity<>(timeSlotService.createTimeSlot(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotResponse> update(@PathVariable Long id, @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(timeSlotService.updateTimeSlot(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }

    // Dodaj u TimeSlotController
    @Operation(summary = "Rezerviši mesto u terminu [LOGIKA]")
    @PostMapping("/{id}/book")
    public ResponseEntity<String> bookSpot(@PathVariable Long id) {
        timeSlotService.bookSpotInTimeSlot(id);
        return ResponseEntity.ok("Uspešno rezervisano mesto!");
    }

    @Operation(summary = "Prikaži samo dostupne termine u budućnosti [FILTRIRANJE]")
    @GetMapping("/available")
    public ResponseEntity<List<TimeSlotResponse>> getAvailable() {
        return ResponseEntity.ok(timeSlotService.getAvailableTimeSlots());
    }
}
