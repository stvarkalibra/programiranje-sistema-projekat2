package org.example.finansijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.finansijeservice.dto.BookingDTO;
import org.example.finansijeservice.dto.InvoiceRequest;
import org.example.finansijeservice.dto.InvoiceResponse;
import org.example.finansijeservice.dto.InvoiceUpdateRequest;
import org.example.finansijeservice.service.InvoicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:8080")
public class InvoicesController {

    private final InvoicesService invoicesService;

    public InvoicesController(InvoicesService invoicesService) {
        this.invoicesService = invoicesService;
    }

    @GetMapping("/{id}")
    ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable
            @Schema(description = "Invoice ID", example = "1")
            Long id) {
        return ResponseEntity.ok(invoicesService.getInvoiceById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create invoice",
            description = "Creates a new invoice and calculates totalAmount = subtotal × (1 + taxRate / 100)"
    )
    @ApiResponse(responseCode = "201", description = "Invoice created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "TaxRate not found")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestBody @Valid InvoiceRequest request) {

        InvoiceResponse response = invoicesService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "List invoices",
            description = "Returns all invoices. Optionally filter by status and/or bookingId."
    )
    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @RequestParam(required = false)
            @Schema(description = "Filter by invoice status", example = "UNPAID")
            String status,

            @RequestParam(required = false)
            @Schema(description = "Filter by booking ID", example = "1")
            Long bookingId) {

        List<InvoiceResponse> invoices = invoicesService.getInvoices(status, bookingId);
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update invoice",
            description = "Updates invoice status. Cannot update a cancelled invoice."
    )
    @ApiResponse(responseCode = "200", description = "Invoice updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status value")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable
            @Schema(description = "Invoice ID", example = "1")
            Long id,

            @RequestBody @Valid InvoiceUpdateRequest request) {

        InvoiceResponse response = invoicesService.updateInvoice(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookings")
    @Operation(
            summary = "Get all bookings",
            description = "Returns all bookings from rezervacije-service via Feign client"
    )
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(invoicesService.getAllBookings());
    }

    @GetMapping("/bookings/{id}/nights")
    @Operation(
            summary = "Get number of nights for a booking",
            description = "Returns the number of nights for a given booking via Feign client"
    )
    @ApiResponse(responseCode = "200", description = "Nights calculated successfully")
    @ApiResponse(responseCode = "404", description = "Booking not found")
    public ResponseEntity<Long> getBookingNights(
            @PathVariable
            @Schema(description = "Booking ID", example = "1")
            Long id) {
        return ResponseEntity.ok(invoicesService.getBookingNights(id));
    }

    @GetMapping("/partially-paid")
    @Operation(
            summary = "Get partially paid invoices",
            description = "Returns UNPAID invoices where sum of transactions is less than total amount"
    )
    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<List<InvoiceResponse>> getPartiallyPaidInvoices() {
        return ResponseEntity.ok(invoicesService.getPartiallyPaidInvoices());
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter invoices by tax name and minimum amount",
            description = "Returns non-cancelled invoices for a given tax type above a minimum total amount, sorted descending by total"
    )
    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByTaxAndMinAmount(
            @RequestParam
            @Schema(description = "Tax name to filter by", example = "PDV 10%")
            String taxName,

            @RequestParam
            @Schema(description = "Minimum total amount", example = "100.0")
            Double minAmount) {

        return ResponseEntity.ok(invoicesService.getInvoicesByTaxNameAndMinAmount(taxName, minAmount));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši invoice iz sistema")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Schema(description = "Invoice ID", example = "1")
            Long id) {
        invoicesService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

}



