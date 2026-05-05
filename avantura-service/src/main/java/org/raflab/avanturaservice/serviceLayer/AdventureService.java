package org.raflab.avanturaservice.serviceLayer;

import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.AdventureRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.AdventureCategory;
import org.raflab.avanturaservice.repositoryLayer.AdventureCategoryRepository;
import org.raflab.avanturaservice.repositoryLayer.AdventureRepository;
import org.raflab.avanturaservice.utils.AdventureEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdventureService {

    private final AdventureRepository adventureRepository;
    private final AdventureCategoryRepository categoryRepository;
    private final AdventureEntityMapper adventureEntityMapper;

    @Transactional
    public AdventureResponse createAdventure(AdventureRequest request) {
        AdventureCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kategorija sa ID " + request.getCategoryId() + " nije pronađena."
                ));

        if (adventureRepository.existsByTitle(request.getTitle())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Avantura sa naslovom '" + request.getTitle() + "' već postoji."
            );
        }

        if ("Extreme".equalsIgnoreCase(request.getDifficultyLevel()) && request.getBasePrice() < 150.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Avanture sa nivoom 'Extreme' moraju imati bazičnu cenu od minimalno 150.0."
            );
        }

        Adventure adventure = adventureEntityMapper.toEntity(request, category);
        Adventure saved = adventureRepository.save(adventure);
        return adventureEntityMapper.toResponse(saved);
    }

    public List<AdventureResponse> getAllAdventures() {
        return adventureRepository.findAll().stream()
                .map(adventureEntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdventureResponse getAdventureById(Long id) {
        return adventureRepository.findById(id)
                .map(adventureEntityMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura sa ID " + id + " ne postoji."
                ));
    }

    @Transactional
    public AdventureResponse updateAdventure(Long id, AdventureRequest request) {
        Adventure existing = adventureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Izmena nemoguća, avantura sa ID " + id + " ne postoji."
                ));

        AdventureCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kategorija nije pronađena."
                ));

        if (!existing.getTitle().equals(request.getTitle()) && adventureRepository.existsByTitle(request.getTitle())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Naslov '" + request.getTitle() + "' je već zauzet od strane druge avanture."
            );
        }

        if ("Extreme".equalsIgnoreCase(request.getDifficultyLevel()) && request.getBasePrice() < 150.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nije moguće postaviti cenu ispod 150.0 za Extreme nivo."
            );
        }

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setDifficultyLevel(request.getDifficultyLevel());
        existing.setBasePrice(request.getBasePrice());
        existing.setAdventureCategory(category);

        return adventureEntityMapper.toResponse(adventureRepository.save(existing));
    }

    @Transactional
    public void deleteAdventure(Long id) {
        if (!adventureRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Brisanje neuspelo, avantura sa ID " + id + " nije u bazi."
            );
        }
        adventureRepository.deleteById(id);
    }

    public Double calculateAdventurePrice(Long id, Double discountPercentage) {
        if (discountPercentage == null || discountPercentage < 0 || discountPercentage > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Popust mora biti između 0 i 100%."
            );
        }

        Adventure adventure = adventureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura nije nađena."
                ));

        return adventure.getBasePrice() * (1 - discountPercentage / 100);
    }

    public List<AdventureResponse> filterAdventures(Long categoryId, Double maxPrice, String difficulty) {
        return adventureRepository.findAll().stream()
                .filter(a -> categoryId == null || a.getAdventureCategory().getId().equals(categoryId))
                .filter(a -> maxPrice == null || a.getBasePrice() <= maxPrice)
                .filter(a -> difficulty == null || a.getDifficultyLevel().equalsIgnoreCase(difficulty))
                .map(adventureEntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AdventureResponse> getPremiumAdventures(Long guideId, Double minPrice) {
        if (guideId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide ID je obavezan.");
        }
        if (minPrice == null || minPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimalna cena mora biti 0 ili veća.");
        }

        return adventureRepository.findByGuideId(guideId).stream()
                .filter(a -> a.getBasePrice() >= minPrice)
                .map(adventureEntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Double getTotalGuideRevenue(Long guideId) {
        if (guideId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide ID je obavezan.");
        }

        return adventureRepository.findByGuideId(guideId).stream()
                .mapToDouble(Adventure::getBasePrice)
                .sum();
    }

    public Boolean isGuideOverloaded(Long guideId) {
        if (guideId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide ID je obavezan.");
        }

        return adventureRepository.findByGuideId(guideId).size() > 3;
    }

    public List<AdventureResponse> getAdventuresByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID je obavezan.");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija sa ID " + categoryId + " ne postoji.");
        }

        return adventureRepository.findByCategoryId(categoryId).stream()
                .map(adventureEntityMapper::toResponse)
                .collect(Collectors.toList());
    }
}