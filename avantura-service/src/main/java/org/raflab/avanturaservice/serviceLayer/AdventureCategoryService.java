package org.raflab.avanturaservice.serviceLayer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.AdventureCategoryRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureCategoryResponse;
import org.raflab.avanturaservice.entityLayer.AdventureCategory;
import org.raflab.avanturaservice.repositoryLayer.AdventureCategoryRepository;
import org.raflab.avanturaservice.utils.AdventureCategoryEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdventureCategoryService {

    private final AdventureCategoryRepository categoryRepository;
    private final AdventureCategoryEntityMapper mapper;

    @Transactional
    public AdventureCategoryResponse createAdventureCategory(AdventureCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kategorija sa imenom '" + request.getName() + "' već postoji."
            );
        }

        AdventureCategory category = mapper.toEntity(request);
        return mapper.toResponse(categoryRepository.save(category));
    }

    public List<AdventureCategoryResponse> getAllAdvetureCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdventureCategoryResponse getAdventureCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kategorija ID " + id + " nije pronađena."
                ));
    }

    @Transactional
    public AdventureCategoryResponse updateAdventureCategory(Long id, AdventureCategoryRequest request) {
        AdventureCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kategorija ID " + id + " ne postoji."
                ));

        if (!existing.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ime '" + request.getName() + "' je već zauzeto."
            );
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        return mapper.toResponse(categoryRepository.save(existing));
    }

    @Transactional
    public void deleteAdventureCategory(Long id) {
        AdventureCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kategorija ne postoji."
                ));

        if (category.getAdventureList() != null && !category.getAdventureList().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nemoguće obrisati: Postoje avanture vezane za ovu kategoriju."
            );
        }

        categoryRepository.deleteById(id);
    }

    public String getCategoryPopularityReport(Long categoryId) {
        AdventureCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Greška: Kategorija ne postoji."
                ));

        int count = category.getAdventureList() != null ? category.getAdventureList().size() : 0;

        if (count == 0) {
            return "Kategorija '" + category.getName() + "' trenutno nema aktivnih avantura.";
        }
        if (count > 5) {
            return "Kategorija '" + category.getName() + "' je SUPER POPULARNA (" + count + " avantura).";
        }
        return "Kategorija '" + category.getName() + "' ima " + count + " aktivnih avantura.";
    }
}