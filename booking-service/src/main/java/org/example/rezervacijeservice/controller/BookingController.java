package org.example.rezervacijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.rezervacijeservice.dto.BookingRequest;
import org.example.rezervacijeservice.dto.BookingResponse;
import org.example.rezervacijeservice.dto.UpdateBookingDTO;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.service.BookingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:8080")
@RefreshScope
public class BookingController {



    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Value("${custom.welcome-message}")
    private String message;

    @GetMapping("/api/v5/orders/welcome")
    public String getMessage() { return message; }

    @GetMapping
    @Operation(
            summary = "List bookings",
            description = "Returns all bookings. Optionally filter by unit ID and/or date (returns bookings where the unit is booked on that date)."
    )
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<List<BookingResponse>> getBookings(
            @RequestParam(required = false)
            @Schema(description = "Filter by unit ID", example = "1")
            Long unitId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Schema(description = "Filter by date — returns bookings whose range covers this date (ISO format: yyyy-MM-dd)", example = "2025-06-15")
            LocalDate date) {

        List<BookingResponse> bookings;
        if (unitId != null && date != null) {
            bookings = bookingService.getBookingsByUnitIdAndDate(unitId, date);
        } else if (unitId != null) {
            bookings = bookingService.getBookingsByUnitId(unitId);
        } else if (date != null) {
            bookings = bookingService.getBookingsByDate(date);
        } else {
            bookings = bookingService.getAllBookings();
        }
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    @ApiResponse(responseCode = "200", description = "Booking retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create booking",
            description = "Creates a new booking with PENDING status"
    )
    @ApiResponse(responseCode = "201", description = "Booking created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking details")
    @ApiResponse(responseCode = "200", description = "Booking updated successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id,
            @RequestBody @Valid UpdateBookingDTO request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update booking status",
            description = "Changes booking status (PENDING → CONFIRMED → COMPLETED or CANCELLED)"
    )
    @ApiResponse(responseCode = "200", description = "Booking status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id,
            @RequestParam
            @Schema(description = "New booking status", example = "CONFIRMED", allowableValues = {"PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"})
            String status) {
        BookingStatus newStatus = BookingStatus.valueOf(status);
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, newStatus));
    }

//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete booking")
//    @ApiResponse(responseCode = "204", description = "Booking deleted successfully")
//    @ApiResponse(responseCode = "404", description = "Booking not found")
//    public ResponseEntity<Void> deleteBooking(
//            @PathVariable
//            @Schema(description = "Booking ID", example = "1")
//            Long id) {
//        bookingService.deleteBooking(id);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/{id}/nights")
    @Operation(
            summary = "Calculate number of nights",
            description = "Calculates the number of nights for a booking (used for pricing calculations)"
    )
    @ApiResponse(responseCode = "200", description = "Nights calculated successfully")
    public ResponseEntity<Long> calculateNights(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id) {
        Long nights = bookingService.calculateNightsStay(id);
        return ResponseEntity.ok(nights);
    }

    @GetMapping("/{id}/refund")
    @Operation(
            summary = "Calculate cancellation refund",
            description = "Returns the refund amount based on the booking's cancellation policy"
    )
    @ApiResponse(responseCode = "200", description = "Refund calculated successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    public ResponseEntity<Double> calculateRefund(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id) {
        return ResponseEntity.ok(bookingService.calculateCancellationRefund(id));
    }
}
