package com.ecotravel.accommodation.service;

import com.ecotravel.accommodation.dto.request.PriceTierRequestDTO;
import com.ecotravel.accommodation.dto.response.PriceTierResponseDTO;
import com.ecotravel.accommodation.model.PriceTier;
import com.ecotravel.accommodation.model.Unit;
import com.ecotravel.accommodation.repository.PriceTierRepository;
import com.ecotravel.accommodation.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceTierService {

    private final PriceTierRepository priceTierRepository;
    private final UnitRepository unitRepository;

    public List<PriceTierResponseDTO> getAll() {
        return priceTierRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PriceTierResponseDTO> getByUnitId(Long unitId) {
        return priceTierRepository.findByUnit_Id(unitId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PriceTierResponseDTO create(PriceTierRequestDTO dto) {
        if (!dto.getStartDate().isBefore(dto.getEndDate())) {
            throw new IllegalArgumentException("Datum pocetka mora biti prije datuma kraja sezone");
        }

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() -> new RuntimeException("Smjestajna jedinica sa ID " + dto.getUnitId() + " nije pronadjena"));

        PriceTier tier = new PriceTier();
        tier.setUnit(unit);
        tier.setName(dto.getName());
        tier.setMultiplier(dto.getMultiplier());
        tier.setStartDate(dto.getStartDate());
        tier.setEndDate(dto.getEndDate());

        return convertToDTO(priceTierRepository.save(tier));
    }

    public void delete(Long id) {
        if (!priceTierRepository.existsById(id)) {
            throw new RuntimeException("Cjenovnik sa ID " + id + " nije pronadjen");
        }
        priceTierRepository.deleteById(id);
    }

    private PriceTierResponseDTO convertToDTO(PriceTier tier) {
        PriceTierResponseDTO dto = new PriceTierResponseDTO();
        dto.setId(tier.getId());
        dto.setUnitId(tier.getUnit().getId());
        dto.setUnitName(tier.getUnit().getName());
        dto.setName(tier.getName());
        dto.setMultiplier(tier.getMultiplier());
        dto.setStartDate(tier.getStartDate());
        dto.setEndDate(tier.getEndDate());
        // Kalkulacija sezonske cijene
        dto.setCalculatedPricePerNight(
                Math.round(tier.getUnit().getBasePricePerNight() * tier.getMultiplier() * 100.0) / 100.0
        );
        return dto;
    }
}