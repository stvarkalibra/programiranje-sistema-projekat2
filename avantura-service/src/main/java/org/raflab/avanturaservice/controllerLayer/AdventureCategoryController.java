package org.raflab.avanturaservice.controllerLayer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.AdventureCategoryRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureCategoryResponse;
import org.raflab.avanturaservice.serviceLayer.AdventureCategoryService;
import org.raflab.avanturaservice.serviceLayer.AdventureReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Adventure Category Controller", description = "Endpointi za upravljanje kategorijama")
public class AdventureCategoryController {

    private final AdventureCategoryService categoryService;

    @Operation(summary = "Prikaz svih kategorija")
    @GetMapping
    public ResponseEntity<List<AdventureCategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAllAdvetureCategories());
    }

    @Operation(summary = "Prikaz kategorije po ID-u")
    @GetMapping("/{id}")
    public ResponseEntity<AdventureCategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getAdventureCategoryById(id));
    }

    @Operation(summary = "Kreiranje nove kategorije")
    @PostMapping
    public ResponseEntity<AdventureCategoryResponse> create(@Valid @RequestBody AdventureCategoryRequest request) {
        return new ResponseEntity<>(categoryService.createAdventureCategory(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmena postojeće kategorije")
    @PutMapping("/{id}")
    public ResponseEntity<AdventureCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody AdventureCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateAdventureCategory(id, request));
    }

    @Operation(summary = "Brisanje kategorije")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteAdventureCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Dodaj u AdventureCategoryController
    @Operation(summary = "Izveštaj o popularnosti kategorije [LOGIKA]")
    @GetMapping("/{id}/popularity")
    public ResponseEntity<String> getPopularity(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryPopularityReport(id));
    }
}
