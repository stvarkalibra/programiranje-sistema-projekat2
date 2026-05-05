package org.raflab.guideservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.clientLayer.RemoteAdventureDTO;
import org.raflab.guideservice.dtoLayer.request.GuideProfileRequest;
import org.raflab.guideservice.dtoLayer.response.GuideProfileResponse;
import org.raflab.guideservice.serviceLayer.GuideProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
@Tag(name = "Guide Profile Controller", description = "Endpointi za upravljanje profilima vodiča i eksternu komunikaciju")
public class GuideProfileController {

    private final GuideProfileService guideService;

    // --- LOKALNE OPERACIJE (CRUD I LOGIKA VODIČA) ---

    @Operation(summary = "Prikaz svih vodiča")
    @GetMapping
    public ResponseEntity<List<GuideProfileResponse>> getAll() {
        return ResponseEntity.ok(guideService.getAllGuides());
    }

    @Operation(summary = "Prikaz vodiča po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<GuideProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.getGuideById(id));
    }

    @Operation(summary = "Kreiranje novog vodiča")
    @PostMapping
    public ResponseEntity<GuideProfileResponse> create(@Valid @RequestBody GuideProfileRequest request) {
        return new ResponseEntity<>(guideService.createGuide(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena profila vodiča")
    @PutMapping("/{id}")
    public ResponseEntity<GuideProfileResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody GuideProfileRequest request) {
        return ResponseEntity.ok(guideService.updateGuide(id, request));
    }

    @Operation(summary = "Brisanje profila vodiča")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pronađi elite vodiče (rejting iznad proseka sistema)")
    @GetMapping("/elite")
    public ResponseEntity<List<GuideProfileResponse>> getElite() {
        return ResponseEntity.ok(guideService.getEliteGuides());
    }

    @Operation(summary = "Pronađi zamenu za vodiča (isti jezik, visok rejting)")
    @GetMapping("/replacements")
    public ResponseEntity<List<GuideProfileResponse>> getReplacements(@RequestParam Long languageId) {
        return ResponseEntity.ok(guideService.findReplacements(languageId));
    }

    @Operation(summary = "Provera da li vodič sme na teren (rejting + važeća licenca)")
    @GetMapping("/{id}/ready-check")
    public ResponseEntity<Boolean> checkReady(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.isGuideReadyForField(id));
    }

    @Operation(summary = "Dodeli bonus na rejting za poliglote (3+ jezika)")
    @PostMapping("/{id}/apply-bonus")
    public ResponseEntity<GuideProfileResponse> applyBonus(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.applyLanguageBonus(id));
    }

    @Operation(summary = "Statistika raspodele vodiča po kvalitetu (Gold/Silver/Bronze)")
    @GetMapping("/stats/rating-distribution")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(guideService.getRatingDistribution());
    }

    // --- EKSTERNE OPERACIJE (POZIVI KA AVANTURA-SERVICE PREKO FEIGN-A) ---

    @Operation(summary = "Filtriranje avantura određenog vodiča koje su skuplje od zadate cene (OPENFEIGN)")
    @GetMapping("/guide/{guideId}/premium")
    public ResponseEntity<List<RemoteAdventureDTO>> getPremium(
            @PathVariable Long guideId,
            @RequestParam Double minPrice) {
        return ResponseEntity.ok(guideService.getPremiumReport(guideId, minPrice));
    }

    @Operation(summary = "Izračunavanje ukupne sume svih osnovnih cena (basePrice) za sve ture jednog vodiča (OPENFEIGN)")
    @GetMapping("/guide/{guideId}/revenue")
    public ResponseEntity<Double> getRevenue(@PathVariable Long guideId, HttpServletRequest request) {
        System.out.println("TRACE ID = " + request.getHeader("X-Trace-Id"));
        System.out.println("USER = " + request.getHeader("X-Authenticated-User"));
        System.out.println("ROLE = " + request.getHeader("X-User-Role"));
        System.out.println("API KEY = " + request.getHeader("X-API-KEY"));
        return ResponseEntity.ok(guideService.getRevenue(guideId));
    }

    @Operation(summary = "Logička provera opterećenja vodiča na osnovu broja aktivnih tura u bazi (OPENFEIGN)")
    @GetMapping("/guide/{guideId}/check-overload")
    public ResponseEntity<Boolean> checkOverload(@PathVariable Long guideId, HttpServletRequest request) {
        System.out.println("TRACE ID = " + request.getHeader("X-Trace-Id"));
        System.out.println("USER = " + request.getHeader("X-Authenticated-User"));
        System.out.println("ROLE = " + request.getHeader("X-User-Role"));
        System.out.println("API KEY = " + request.getHeader("X-API-KEY"));
        return ResponseEntity.ok(guideService.checkOverload(guideId));
    }

    @Operation(summary = "Dobavljanje svih avantura za određenu kategoriju putem JOIN upita (OPENFEIGN + JOIN)")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RemoteAdventureDTO>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(guideService.getAllInCategory(categoryId));
    }

}