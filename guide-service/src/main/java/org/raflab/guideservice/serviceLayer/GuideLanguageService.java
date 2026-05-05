package org.raflab.guideservice.serviceLayer;

import lombok.RequiredArgsConstructor;
import org.raflab.guideservice.dtoLayer.request.GuideLanguageRequest;
import org.raflab.guideservice.dtoLayer.response.GuideLanguageResponse;
import org.raflab.guideservice.entityLayer.GuideLanguage;
import org.raflab.guideservice.entityLayer.GuideProfile;
import org.raflab.guideservice.entityLayer.Language;
import org.raflab.guideservice.repositoryLayer.GuideLanguageRepository;
import org.raflab.guideservice.repositoryLayer.GuideProfileRepository;
import org.raflab.guideservice.repositoryLayer.LanguageRepository;
import org.raflab.guideservice.utils.GuideLanguageEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideLanguageService {

    private final GuideLanguageRepository guideLanguageRepository;
    private final GuideProfileRepository guideProfileRepository;
    private final LanguageRepository languageRepository;
    private final GuideLanguageEntityMapper guideLanguageEntityMapper;

    @Transactional
    public GuideLanguageResponse createGuideLanguage(GuideLanguageRequest request) {
        GuideProfile guide = guideProfileRepository.findById(request.getGuideProfileId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vodič nije pronađen."
                ));

        Language language = languageRepository.findById(request.getLanguageId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Jezik nije pronađen."
                ));

        if (guideLanguageRepository.findByGuideProfileIdAndLanguageId(
                request.getGuideProfileId(),
                request.getLanguageId()
        ).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vodič već ima registrovan ovaj jezik."
            );
        }

        GuideLanguage guideLanguage = guideLanguageEntityMapper.toEntity(request, guide, language);
        return guideLanguageEntityMapper.toResponse(guideLanguageRepository.save(guideLanguage));
    }

    public List<GuideLanguageResponse> getAllGuideLanguages() {
        return guideLanguageRepository.findAll().stream()
                .map(guideLanguageEntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    public GuideLanguageResponse getGuideLanguageById(Long id) {
        return guideLanguageRepository.findById(id)
                .map(guideLanguageEntityMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Veza ID " + id + " ne postoji."
                ));
    }

    @Transactional
    public GuideLanguageResponse updateGuideLanguage(Long id, GuideLanguageRequest request) {
        GuideLanguage existing = guideLanguageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Veza ne postoji."
                ));

        if (!existing.getLanguage().getId().equals(request.getLanguageId())) {
            if (guideLanguageRepository.findByGuideProfileIdAndLanguageId(
                    request.getGuideProfileId(),
                    request.getLanguageId()
            ).isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Vodič već govori taj jezik."
                );
            }
        }

        GuideProfile guide = guideProfileRepository.findById(request.getGuideProfileId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vodič nije pronađen."
                ));

        Language language = languageRepository.findById(request.getLanguageId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Jezik nije pronađen."
                ));

        existing.setGuideProfile(guide);
        existing.setLanguage(language);
        existing.setProficiencyLevel(request.getProficiencyLevel());

        return guideLanguageEntityMapper.toResponse(guideLanguageRepository.save(existing));
    }

    @Transactional
    public void deleteGuideLanguage(Long id) {
        if (!guideLanguageRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Brisanje neuspelo."
            );
        }
        guideLanguageRepository.deleteById(id);
    }

    public boolean canLeadInLanguage(Long guideId, Long languageId) {
        return guideLanguageRepository.findByGuideProfileIdAndLanguageId(guideId, languageId)
                .map(gl -> List.of("B2", "C1", "C2", "Native").contains(gl.getProficiencyLevel()))
                .orElse(false);
    }

    public long getActiveLanguageCount(Long guideId) {
        if (!guideProfileRepository.existsById(guideId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vodič nije pronađen."
            );
        }
        return guideLanguageRepository.countByGuideProfileId(guideId);
    }
}