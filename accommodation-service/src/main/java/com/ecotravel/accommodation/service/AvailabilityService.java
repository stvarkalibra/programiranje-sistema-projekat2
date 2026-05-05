package com.ecotravel.accommodation.service;

import com.ecotravel.accommodation.dto.request.AvailabilityRequestDTO;
import com.ecotravel.accommodation.dto.response.AvailabilityResponseDTO;
import com.ecotravel.accommodation.model.AvailabilityCalendar;
import com.ecotravel.accommodation.model.Unit;
import com.ecotravel.accommodation.repository.AvailabilityCalendarRepository;
import com.ecotravel.accommodation.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityCalendarRepository availabilityCalendarRepository;
    private final UnitRepository unitRepository;

    public List<AvailabilityResponseDTO> getByUnitId(Long unitId) {
        return availabilityCalendarRepository.findByUnit_Id(unitId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean checkAvailability(Long unitId, LocalDate startDate, LocalDate endDate) {
        if (!unitRepository.existsById(unitId)) {
            throw new RuntimeException("Smjestajna jedinica sa ID " + unitId + " nije pronadjena");
        }
        // Vraca TRUE ako je slobodna, FALSE ako je zauzeta
        return !availabilityCalendarRepository.existsOverlappingBooking(unitId, startDate, endDate);
    }

    public AvailabilityResponseDTO blockDates(AvailabilityRequestDTO dto) {
        if (!dto.getStartDate().isBefore(dto.getEndDate())) {
            throw new IllegalArgumentException("Datum pocetka mora biti prije datuma kraja");
        }

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() -> new RuntimeException("Smjestajna jedinica sa ID " + dto.getUnitId() + " nije pronadjena"));

        boolean isOccupied = availabilityCalendarRepository
                .existsOverlappingBooking(dto.getUnitId(), dto.getStartDate(), dto.getEndDate());

        if (isOccupied) {
            throw new IllegalStateException("Smjestajna jedinica je vec zauzeta u odabranom periodu");
        }

        AvailabilityCalendar entry = new AvailabilityCalendar();
        entry.setUnit(unit);
        entry.setStartDate(dto.getStartDate());
        entry.setEndDate(dto.getEndDate());
        entry.setReason(dto.getReason().toUpperCase());

        return convertToDTO(availabilityCalendarRepository.save(entry));
    }

    public void delete(Long id) {
        if (!availabilityCalendarRepository.existsById(id)) {
            throw new RuntimeException("Zapis u kalendaru sa ID " + id + " nije pronadjen");
        }
        availabilityCalendarRepository.deleteById(id);
    }

    private AvailabilityResponseDTO convertToDTO(AvailabilityCalendar entry) {
        AvailabilityResponseDTO dto = new AvailabilityResponseDTO();
        dto.setId(entry.getId());
        dto.setUnitId(entry.getUnit().getId());
        dto.setUnitName(entry.getUnit().getName());
        dto.setStartDate(entry.getStartDate());
        dto.setEndDate(entry.getEndDate());
        dto.setReason(entry.getReason());
        return dto;
    }
}