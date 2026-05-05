package org.raflab.guideservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.dtoLayer.request.CertificationRequest;
import org.raflab.guideservice.dtoLayer.response.CertificationResponse;
import org.raflab.guideservice.serviceLayer.CertificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
@Tag(name = "Certification Controller", description = "Upravljanje sertifikatima vodiča")
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(summary = "Prikaz svih sertifikata")
    @GetMapping
    public ResponseEntity<List<CertificationResponse>> getAll() {
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }

    @Operation(summary = "Prikaz jednog sertifikata po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<CertificationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @Operation(summary = "Dodavanje novog sertifikata")
    @PostMapping
    public ResponseEntity<CertificationResponse> create(@Valid @RequestBody CertificationRequest request) {
        return new ResponseEntity<>(certificationService.createCertification(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena sertifikata")
    @PutMapping("/{id}")
    public ResponseEntity<CertificationResponse> update(@PathVariable Long id, @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(certificationService.updateCertification(id, request));
    }

    @Operation(summary = "Brisanje sertifikata")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista emailova vodiča čije su licence istekle (Admin alert)")
    @GetMapping("/risk-report/expired-emails")
    public ResponseEntity<List<String>> getRiskEmails() {
        return ResponseEntity.ok(certificationService.getGuidesWithExpiredCertificates());
    }
}