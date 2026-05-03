package org.example.rezervacijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.rezervacijeservice.dto.AddOnItemResponse;
import org.example.rezervacijeservice.service.AddOnItemsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/add-on-items")
@CrossOrigin(origins = "http://localhost:8080")
public class AddOnItemsController {

    private final AddOnItemsService addOnItemsService;

    public AddOnItemsController(AddOnItemsService addOnItemsService) {
        this.addOnItemsService = addOnItemsService;
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(
            summary = "Get all add-on items for a booking",
            description = "Returns all add-on items associated with a specific booking"
    )
    @ApiResponse(responseCode = "200", description = "Items retrieved successfully")
    public ResponseEntity<List<AddOnItemResponse>> getAddOnItemsByBooking(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long bookingId) {
        return ResponseEntity.ok(addOnItemsService.getAddOnItemsByBookingId(bookingId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get add-on item by ID")
    @ApiResponse(responseCode = "200", description = "Item retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<AddOnItemResponse> getAddOnItemById(
            @PathVariable
            @Schema(description = "Add-on item ID", example = "1")
            Long id) {
        return ResponseEntity.ok(addOnItemsService.getAddOnItemById(id));
    }

    @PostMapping("/booking/{bookingId}")
    @Operation(
            summary = "Create add-on item for a booking",
            description = "Adds a new additional service item to a booking"
    )
    @ApiResponse(responseCode = "201", description = "Item created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<AddOnItemResponse> createAddOnItem(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long bookingId,
            @RequestBody @Valid AddOnItemResponse dto) {
        AddOnItemResponse response = addOnItemsService.createAddOnItem(bookingId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update add-on item")
    @ApiResponse(responseCode = "200", description = "Item updated successfully")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<AddOnItemResponse> updateAddOnItem(
            @PathVariable
            @Schema(description = "Add-on item ID", example = "1")
            Long id,
            @RequestBody @Valid AddOnItemResponse dto) {
        return ResponseEntity.ok(addOnItemsService.updateAddOnItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete add-on item")
    @ApiResponse(responseCode = "204", description = "Item deleted successfully")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<Void> deleteAddOnItem(
            @PathVariable
            @Schema(description = "Add-on item ID", example = "1")
            Long id) {
        addOnItemsService.deleteAddOnItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/booking/{bookingId}/total")
    @Operation(
            summary = "Calculate total add-on price for booking",
            description = "Calculates sum of all add-on items for a specific booking"
    )
    @ApiResponse(responseCode = "200", description = "Total calculated successfully")
    public ResponseEntity<Double> getTotalAddOnPrice(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long bookingId) {
        Double total = addOnItemsService.calculateTotalAddOnPrice(bookingId);
        return ResponseEntity.ok(total);
    }
}
