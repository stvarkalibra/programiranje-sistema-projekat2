package org.raflab.guideservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.dtoLayer.request.GuideLanguageRequest;
import org.raflab.guideservice.dtoLayer.response.GuideLanguageResponse;
import org.raflab.guideservice.serviceLayer.GuideLanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guide-languages")
@RequiredArgsConstructor
@Tag(name = "Guide Language Controller", description = "Upravljanje jezicima koje vodiči govore")
public class GuideLanguageController {

    private final GuideLanguageService guideLanguageService;

    @Operation(summary = "Prikaz svih veza vodič-jezik")
    @GetMapping
    public ResponseEntity<List<GuideLanguageResponse>> getAll() {
        return ResponseEntity.ok(guideLanguageService.getAllGuideLanguages());
    }

    @Operation(summary = "Prikaz veze po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<GuideLanguageResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(guideLanguageService.getGuideLanguageById(id));
    }

    @Operation(summary = "Dodeli jezik vodiču")
    @PostMapping
    public ResponseEntity<GuideLanguageResponse> create(@Valid @RequestBody GuideLanguageRequest request) {
        return new ResponseEntity<>(guideLanguageService.createGuideLanguage(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena nivoa znanja ili jezika")
    @PutMapping("/{id}")
    public ResponseEntity<GuideLanguageResponse> update(@PathVariable Long id, @Valid @RequestBody GuideLanguageRequest request) {
        return ResponseEntity.ok(guideLanguageService.updateGuideLanguage(id, request));
    }

    @Operation(summary = "Ukloni jezik sa profila vodiča")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guideLanguageService.deleteGuideLanguage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Provera kompetencije za vođenje ture na specifičnom jeziku (B2+ nivo)")
    @GetMapping("/check-fluency")
    public ResponseEntity<Boolean> checkFluency(@RequestParam Long guideId, @RequestParam Long languageId) {
        return ResponseEntity.ok(guideLanguageService.canLeadInLanguage(guideId, languageId));
    }

    @Operation(summary = "Broj aktivnih jezika koje vodič koristi")
    @GetMapping("/count/{guideId}")
    public ResponseEntity<Long> getLangCount(@PathVariable Long guideId) {
        return ResponseEntity.ok(guideLanguageService.getActiveLanguageCount(guideId));
    }
}