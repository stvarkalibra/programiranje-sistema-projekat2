package org.example.rezervacijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.rezervacijeservice.model.BookingStatusLog;
import org.example.rezervacijeservice.service.BookingStatusLogsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-status-logs")
@CrossOrigin(origins = "http://localhost:8080")
public class BookingStatusLogsController {

    private final BookingStatusLogsService bookingStatusLogsService;

    public BookingStatusLogsController(BookingStatusLogsService bookingStatusLogsService) {
        this.bookingStatusLogsService = bookingStatusLogsService;
    }

    @GetMapping
    @Operation(summary = "Get all booking status logs")
    @ApiResponse(responseCode = "200", description = "Logs retrieved successfully")
    public ResponseEntity<List<BookingStatusLog>> getAllStatusLogs() {
        return ResponseEntity.ok(bookingStatusLogsService.getAllStatusLogs());
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(
            summary = "Get status history for a booking",
            description = "Returns all status changes for a specific booking, sorted by most recent first"
    )
    @ApiResponse(responseCode = "200", description = "Status history retrieved successfully")
    public ResponseEntity<List<BookingStatusLog>> getStatusHistory(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long bookingId) {
        return ResponseEntity.ok(bookingStatusLogsService.getStatusHistoryForBooking(bookingId));
    }
}
