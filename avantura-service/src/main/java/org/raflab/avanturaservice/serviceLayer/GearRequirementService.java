package org.raflab.avanturaservice.serviceLayer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.GearRequirementRequest;
import org.raflab.avanturaservice.dtoLayer.response.GearRequirementResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.GearRequirement;
import org.raflab.avanturaservice.repositoryLayer.AdventureRepository;
import org.raflab.avanturaservice.repositoryLayer.GearRequirementRepository;
import org.raflab.avanturaservice.utils.GearRequirementEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GearRequirementService {

    private final GearRequirementRepository gearRepository;
    private final AdventureRepository adventureRepository;
    private final GearRequirementEntityMapper mapper;

    @Transactional
    public GearRequirementResponse createGearRequirement(GearRequirementRequest request) {
        validateGearRequest(request);

        Adventure adventure = adventureRepository.findById(request.getAdventureId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura sa ID " + request.getAdventureId() + " nije pronađena."
                ));

        if (gearRepository.existsByItemNameAndAdventureId(request.getItemName(), request.getAdventureId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Oprema '" + request.getItemName() + "' je već dodata za ovu avanturu."
            );
        }

        GearRequirement gear = mapper.toEntity(request, adventure);
        return mapper.toResponse(gearRepository.save(gear));
    }

    public List<GearRequirementResponse> getAllGearRequirements() {
        return gearRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public GearRequirementResponse getGearRequirementById(Long id) {
        GearRequirement gear = gearRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Oprema ID " + id + " nije pronađena."
                ));

        return mapper.toResponse(gear);
    }

    @Transactional
    public GearRequirementResponse updateGearRequirement(Long id, GearRequirementRequest request) {
        validateGearRequest(request);

        GearRequirement existing = gearRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Oprema ID " + id + " ne postoji."
                ));

        if (!existing.getAdventure().getId().equals(request.getAdventureId())) {
            Adventure newAdventure = adventureRepository.findById(request.getAdventureId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Nova avantura nije pronađena."
                    ));
            existing.setAdventure(newAdventure);
        }

        existing.setItemName(request.getItemName());
        existing.setMandatory(request.getIsMandatory());

        return mapper.toResponse(gearRepository.save(existing));
    }

    @Transactional
    public void deleteGearRequirement(Long id) {
        if (!gearRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Brisanje neuspelo: Stavka opreme ne postoji."
            );
        }

        gearRepository.deleteById(id);
    }

    private void validateGearRequest(GearRequirementRequest request) {
        if (request.getItemName() == null || request.getItemName().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Naziv opreme ne sme biti prazan."
            );
        }

        if (request.getItemName().matches("-?\\d+(\\.\\d+)?")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Naziv opreme ne može biti samo broj."
            );
        }

        if (request.getAdventureId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID avanture je obavezan."
            );
        }

        if (request.getIsMandatory() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Polje isMandatory je obavezno."
            );
        }
    }

    public String checkSafetyGearCompliance(Long adventureId) {
        Adventure adventure = adventureRepository.findById(adventureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura nije nađena."
                ));

        long mandatoryGearCount = gearRepository.findAll().stream()
                .filter(g -> g.getAdventure().getId().equals(adventureId) && g.isMandatory())
                .count();

        if ("Extreme".equalsIgnoreCase(adventure.getDifficultyLevel()) && mandatoryGearCount < 3) {
            return "UPOZORENJE: Extreme avantura mora imati barem 3 stavke obavezne opreme! Trenutno: " + mandatoryGearCount;
        }

        return "Oprema je u skladu sa nivoom težine.";
    }

    public Long countMandatoryGear(Long adventureId) {
        if (!adventureRepository.existsById(adventureId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Avantura sa ID " + adventureId + " nije pronađena."
            );
        }

        return gearRepository.findAll().stream()
                .filter(gear -> gear.getAdventure().getId().equals(adventureId))
                .filter(GearRequirement::isMandatory)
                .count();
    }
}