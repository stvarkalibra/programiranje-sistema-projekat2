package org.example.rezervacijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.rezervacijeservice.dto.GuestResponse;
import org.example.rezervacijeservice.service.GuestsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "http://localhost:8080")
public class GuestsController {
    private final GuestsService guestsService;

    public GuestsController(GuestsService guestsService) {
        this.guestsService = guestsService;
    }

    @GetMapping
    @Operation(
            summary = "List guests",
            description = "Returns all guests. Optionally filter by email."
    )
    @ApiResponse(responseCode = "200", description = "Guests retrieved successfully")
    public ResponseEntity<List<GuestResponse>> getGuests(
            @RequestParam(required = false)
            @Schema(description = "Filter by email", example = "marko@example.com")
            String email) {

        List<GuestResponse> guests;
        if (email != null) {
            guests = guestsService.searchGuestsByEmail(email);
        } else {
            guests = guestsService.getAllGuests();
        }
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guest by ID")
    @ApiResponse(responseCode = "200", description = "Guest retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<GuestResponse> getGuestById(
            @PathVariable
            @Schema(description = "Guest ID", example = "1")
            Long id) {
        return ResponseEntity.ok(guestsService.getGuestById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create guest",
            description = "Creates a new guest record"
    )
    @ApiResponse(responseCode = "201", description = "Guest created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<GuestResponse> createGuest(@RequestBody @Valid GuestResponse dto) {
        GuestResponse response = guestsService.createGuest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update guest")
    @ApiResponse(responseCode = "200", description = "Guest updated successfully")
    @ApiResponse(responseCode = "404", description = "Guest not found")
    public ResponseEntity<GuestResponse> updateGuest(
            @PathVariable
            @Schema(description = "Guest ID", example = "1")
            Long id,
            @RequestBody @Valid GuestResponse dto) {
        return ResponseEntity.ok(guestsService.updateGuest(id, dto));
    }

//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete guest")
//    @ApiResponse(responseCode = "204", description = "Guest deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Guest not found")
//    @ApiResponse(responseCode = "409", description = "Cannot delete guest with active bookings")
//    public ResponseEntity<?> deleteGuest(
//            @PathVariable
//            @Schema(description = "Guest ID", example = "1")
//            Long id) {
//        try {
//            guestsService.deleteGuest(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalStateException e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Conflict");
//            errorResponse.put("message", e.getMessage());
//            errorResponse.put("status", 409);
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
//        }
//    }
}
