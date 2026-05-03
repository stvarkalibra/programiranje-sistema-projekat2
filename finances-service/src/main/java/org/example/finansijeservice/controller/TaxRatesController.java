package org.example.finansijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.finansijeservice.dto.TaxRateDTO;
import org.example.finansijeservice.service.TaxRatesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tax-rates")
@CrossOrigin(origins = "http://localhost:8080")
public class TaxRatesController {

    private final TaxRatesService taxRatesService;

    public TaxRatesController(TaxRatesService taxRatesService) {
        this.taxRatesService = taxRatesService;
    }

    @GetMapping
    @Operation(
            summary = "List all tax rates",
            description = "Returns all tax rates. Optionally filter by percentage range."
    )
    @ApiResponse(responseCode = "200", description = "Tax rates retrieved successfully")
    public ResponseEntity<List<TaxRateDTO>> getAllTaxRates(
            @RequestParam(required = false)
            @Schema(description = "Minimum tax percentage", example = "0")
            Double minPercentage,

            @RequestParam(required = false)
            @Schema(description = "Maximum tax percentage", example = "30")
            Double maxPercentage) {

        List<TaxRateDTO> taxRates;
        if (minPercentage != null && maxPercentage != null) {
            taxRates = taxRatesService.getTaxRatesByPercentageRange(minPercentage, maxPercentage);
        } else {
            taxRates = taxRatesService.getAllTaxRates();
        }
        return ResponseEntity.ok(taxRates);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tax rate by ID")
    @ApiResponse(responseCode = "200", description = "Tax rate retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tax rate not found")
    public ResponseEntity<TaxRateDTO> getTaxRateById(
            @PathVariable
            @Schema(description = "Tax rate ID", example = "1")
            Long id) {
        return ResponseEntity.ok(taxRatesService.getTaxRateById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create tax rate",
            description = "Creates a new tax rate"
    )
    @ApiResponse(responseCode = "201", description = "Tax rate created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<TaxRateDTO> createTaxRate(@RequestBody @Valid TaxRateDTO dto) {
        TaxRateDTO response = taxRatesService.createTaxRate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tax rate")
    @ApiResponse(responseCode = "200", description = "Tax rate updated successfully")
    @ApiResponse(responseCode = "404", description = "Tax rate not found")
    public ResponseEntity<TaxRateDTO> updateTaxRate(
            @PathVariable
            @Schema(description = "Tax rate ID", example = "1")
            Long id,
            @RequestBody @Valid TaxRateDTO dto) {
        return ResponseEntity.ok(taxRatesService.updateTaxRate(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tax rate")
    @ApiResponse(responseCode = "204", description = "Tax rate deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tax rate not found")
    public ResponseEntity<Void> deleteTaxRate(
            @PathVariable
            @Schema(description = "Tax rate ID", example = "1")
            Long id) {
        taxRatesService.deleteTaxRate(id);
        return ResponseEntity.noContent().build();
    }
}
