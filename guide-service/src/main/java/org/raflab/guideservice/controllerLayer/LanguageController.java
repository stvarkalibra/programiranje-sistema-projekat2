package org.raflab.guideservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.dtoLayer.request.LanguageRequest;
import org.raflab.guideservice.dtoLayer.response.LanguageResponse;
import org.raflab.guideservice.serviceLayer.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
@Tag(name = "Language Controller", description = "Katalog jezika koje vodiči mogu govoriti")
public class LanguageController {

    private final LanguageService languageService;

    @Operation(summary = "Lista svih jezika")
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getAll() {
        return ResponseEntity.ok(languageService.getAllLanguages());
    }

    @Operation(summary = "Prikaz jezika po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<LanguageResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(languageService.getLanguageById(id));
    }

    @Operation(summary = "Dodavanje novog jezika")
    @PostMapping
    public ResponseEntity<LanguageResponse> create(@Valid @RequestBody LanguageRequest request) {
        return new ResponseEntity<>(languageService.createLanguage(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena jezika")
    @PutMapping("/{id}")
    public ResponseEntity<LanguageResponse> update(@PathVariable Long id, @Valid @RequestBody LanguageRequest request) {
        return ResponseEntity.ok(languageService.updateLanguage(id, request));
    }

    @Operation(summary = "Brisanje jezika")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }
}