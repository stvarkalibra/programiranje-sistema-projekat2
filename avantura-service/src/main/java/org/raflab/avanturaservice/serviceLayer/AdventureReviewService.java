package org.raflab.avanturaservice.serviceLayer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.AdventureReviewRequest;
import org.raflab.avanturaservice.dtoLayer.response.AdventureReviewResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.AdventureReview;
import org.raflab.avanturaservice.repositoryLayer.AdventureRepository;
import org.raflab.avanturaservice.repositoryLayer.AdventureReviewRepository;
import org.raflab.avanturaservice.utils.AdventureReviewEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdventureReviewService {

    private final AdventureReviewRepository reviewRepository;
    private final AdventureRepository adventureRepository;
    private final AdventureReviewEntityMapper mapper;

    @Transactional
    public AdventureReviewResponse createAdventureReview(AdventureReviewRequest request) {
        validateRating(request.getRating());

        Adventure adventure = adventureRepository.findById(request.getAdventureId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura sa ID " + request.getAdventureId() + " nije pronađena."
                ));

        AdventureReview review = mapper.toEntity(request, adventure);
        return mapper.toResponse(reviewRepository.save(review));
    }

    public List<AdventureReviewResponse> getAllAdventureReviews() {
        return reviewRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdventureReviewResponse getAdventureReviewById(Long id) {
        AdventureReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recenzija ID " + id + " nije pronađena."
                ));

        return mapper.toResponse(review);
    }

    @Transactional
    public AdventureReviewResponse updateAdventureReview(Long id, AdventureReviewRequest request) {
        validateRating(request.getRating());

        AdventureReview existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recenzija ID " + id + " ne postoji."
                ));

        if (!existing.getAdventure().getId().equals(request.getAdventureId())) {
            Adventure newAdventure = adventureRepository.findById(request.getAdventureId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Nova avantura nije pronađena."
                    ));
            existing.setAdventure(newAdventure);
        }

        existing.setRating(request.getRating());
        existing.setComment(request.getComment());

        return mapper.toResponse(reviewRepository.save(existing));
    }

    @Transactional
    public void deleteAdventureReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Brisanje neuspelo: Recenzija ne postoji."
            );
        }

        reviewRepository.deleteById(id);
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ocena mora biti ceo broj u opsegu od 1 do 5."
            );
        }
    }

    public String getAdventureRatingStats(Long adventureId) {
        if (!adventureRepository.existsById(adventureId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Avantura sa ID " + adventureId + " nije pronađena."
            );
        }

        List<AdventureReview> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getAdventure().getId().equals(adventureId))
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            return "Avantura još uvek nema ocena.";
        }

        double average = reviews.stream()
                .mapToInt(AdventureReview::getRating)
                .average()
                .orElse(0.0);

        return String.format("Avantura ima %d recenzija sa prosečnom ocenom %.2f", reviews.size(), average);
    }
}