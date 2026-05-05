package org.raflab.avanturaservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.AdventureReviewRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureReviewResponse;
import org.raflab.avanturaservice.serviceLayer.AdventureReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Adventure Review Controller", description = "Endpointi za upravljanje recenzijama")
public class AdventureReviewController {

    private final AdventureReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<AdventureReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAllAdventureReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdventureReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getAdventureReviewById(id));
    }

    @PostMapping
    public ResponseEntity<AdventureReviewResponse> create(@Valid @RequestBody AdventureReviewRequest request) {
        return new ResponseEntity<>(reviewService.createAdventureReview(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdventureReviewResponse> update(@PathVariable Long id, @Valid @RequestBody AdventureReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateAdventureReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.deleteAdventureReview(id);
        return ResponseEntity.noContent().build();
    }

    // Dodaj u AdventureReviewController
    @Operation(summary = "Statistika ocena za avanturu [MATEMATIKA]")
    @GetMapping("/stats/adventure/{adventureId}")
    public ResponseEntity<String> getStats(@PathVariable Long adventureId) {
        return ResponseEntity.ok(reviewService.getAdventureRatingStats(adventureId));
    }
}