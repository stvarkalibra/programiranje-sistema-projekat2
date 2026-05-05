package com.ecotravel.accommodation.service;

import com.ecotravel.accommodation.dto.request.FacilityRequestDTO;
import com.ecotravel.accommodation.dto.response.FacilityResponseDTO;
import com.ecotravel.accommodation.model.Facility;
import com.ecotravel.accommodation.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<FacilityResponseDTO> getAll() {
        return facilityRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FacilityResponseDTO getById(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sadrzaj sa ID " + id + " nije pronadjen"));
        return convertToDTO(facility);
    }

    public FacilityResponseDTO create(FacilityRequestDTO dto) {
        Facility facility = new Facility();
        facility.setName(dto.getName());
        facility.setExtraCost(dto.getExtraCost());
        return convertToDTO(facilityRepository.save(facility));
    }

    public FacilityResponseDTO update(Long id, FacilityRequestDTO dto) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sadrzaj sa ID " + id + " nije pronadjen"));
        facility.setName(dto.getName());
        facility.setExtraCost(dto.getExtraCost());
        return convertToDTO(facilityRepository.save(facility));
    }

    public void delete(Long id) {
        if (!facilityRepository.existsById(id)) {
            throw new RuntimeException("Sadrzaj sa ID " + id + " nije pronadjen");
        }
        facilityRepository.deleteById(id);
    }

    public FacilityResponseDTO convertToDTO(Facility facility) {
        FacilityResponseDTO dto = new FacilityResponseDTO();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setExtraCost(facility.getExtraCost());
        return dto;
    }
}