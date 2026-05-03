package org.example.rezervacijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.rezervacijeservice.dto.CancellationPolicyResponse;
import org.example.rezervacijeservice.service.CancellationPoliciesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellation-policies")
@CrossOrigin(origins = "http://localhost:8080")
public class CancellationPoliciesController {

    private final CancellationPoliciesService cancellationPoliciesService;

    public CancellationPoliciesController(CancellationPoliciesService cancellationPoliciesService) {
        this.cancellationPoliciesService = cancellationPoliciesService;
    }

    @GetMapping
    @Operation(summary = "List all cancellation policies")
    @ApiResponse(responseCode = "200", description = "Policies retrieved successfully")
    public ResponseEntity<List<CancellationPolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(cancellationPoliciesService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cancellation policy by ID")
    @ApiResponse(responseCode = "200", description = "Policy retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<CancellationPolicyResponse> getPolicyById(
            @PathVariable
            @Schema(description = "Cancellation policy ID", example = "1")
            Long id) {
        return ResponseEntity.ok(cancellationPoliciesService.getPolicyById(id));
    }

    @PostMapping
    @Operation(summary = "Create cancellation policy")
    @ApiResponse(responseCode = "201", description = "Policy created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<CancellationPolicyResponse> createPolicy(@RequestBody @Valid CancellationPolicyResponse dto) {
        CancellationPolicyResponse response = cancellationPoliciesService.createPolicy(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cancellation policy")
    @ApiResponse(responseCode = "200", description = "Policy updated successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<CancellationPolicyResponse> updatePolicy(
            @PathVariable
            @Schema(description = "Cancellation policy ID", example = "1")
            Long id,
            @RequestBody @Valid CancellationPolicyResponse dto) {
        return ResponseEntity.ok(cancellationPoliciesService.updatePolicy(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cancellation policy")
    @ApiResponse(responseCode = "204", description = "Policy deleted successfully")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<Void> deletePolicy(
            @PathVariable
            @Schema(description = "Cancellation policy ID", example = "1")
            Long id) {
        cancellationPoliciesService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
