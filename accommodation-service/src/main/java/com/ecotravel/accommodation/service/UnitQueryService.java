package com.ecotravel.accommodation.service;

import com.ecotravel.accommodation.dto.response.UnitSummaryResponseDTO;
import com.ecotravel.accommodation.model.Facility;
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
public class UnitQueryService {

    private final UnitRepository unitRepository;
    private final AvailabilityCalendarRepository availabilityCalendarRepository;

    /**
     * SLOZENA PROVJERA 1: Provjera da li jedinica postoji i da li je odredjenog tipa.
     * Koristi se od strane maintenance servisa da validira radni nalog.
     */
    public UnitSummaryResponseDTO getUnitSummary(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Smjestajna jedinica sa ID " + unitId + " nije pronadjena"));
        return convertToSummaryDTO(unit, null);
    }

    /**
     * SLOZENA PROVJERA 2: Provjera da li je jedinica dostupna za odrzavanje.
     * Logika: jedinica mora biti slobodna (nema booking-a) i mora postojati.
     */
    public UnitSummaryResponseDTO getUnitAvailabilityStatus(Long unitId, LocalDate date) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Smjestajna jedinica sa ID " + unitId + " nije pronadjena"));

        // Provjeri da li postoji booking koji se preklapa sa datumom odrzavanja
        boolean isBooked = availabilityCalendarRepository
                .existsOverlappingBooking(unitId, date, date);

        UnitSummaryResponseDTO dto = convertToSummaryDTO(unit, !isBooked);
        return dto;
    }

    /**
     * SLOZENA PROVJERA 3: Dohvati sve jedinice sa sadrzajima za odredjeni grad.
     * Koristi JOIN FETCH da izbjegne N+1 problem.
     */
    public List<UnitSummaryResponseDTO> getUnitsWithFacilitiesByCity(String city) {
        List<Unit> units = unitRepository.findByCity_WithFacilities(city);

        if (units.isEmpty()) {
            throw new RuntimeException("Nema smjestajnih jedinica u gradu: " + city);
        }

        return units.stream()
                .map(u -> convertToSummaryDTO(u, null))
                .collect(Collectors.toList());
    }

    /**
     * SLOZENI UPIT: Dohvati jedinice po tipu sa filtrom na ukupnu cijenu
     */
    public List<UnitSummaryResponseDTO> getUnitsByTypeAndMaxTotalPrice(String unitType, Double maxTotalPrice) {
        List<Unit> units = unitRepository.findByTypeAndMaxTotalPrice(unitType.toUpperCase(), maxTotalPrice);
        return units.stream()
                .map(u -> convertToSummaryDTO(u, null))
                .collect(Collectors.toList());
    }

    /**
     * SLOZENI UPIT: Dohvati jedinice koje imaju odredjeni sadrzaj
     */
    public List<UnitSummaryResponseDTO> getUnitsByFacility(String facilityName) {
        List<Unit> units = unitRepository.findByFacilityName(facilityName);
        return units.stream()
                .map(u -> convertToSummaryDTO(u, null))
                .collect(Collectors.toList());
    }

    private UnitSummaryResponseDTO convertToSummaryDTO(Unit unit, Boolean isAvailable) {
        UnitSummaryResponseDTO dto = new UnitSummaryResponseDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setUnitType(unit.getUnitType());
        dto.setBasePricePerNight(unit.getBasePricePerNight());

        double facilitiesTotal = 0.0;
        List<String> facilityNames = List.of();
        if (unit.getFacilities() != null) {
            facilitiesTotal = unit.getFacilities().stream()
                    .mapToDouble(Facility::getExtraCost).sum();
            facilityNames = unit.getFacilities().stream()
                    .map(Facility::getName)
                    .collect(Collectors.toList());
        }
        dto.setTotalPriceWithFacilities(unit.getBasePricePerNight() + facilitiesTotal);
        dto.setFacilityNames(facilityNames);

        if (unit.getAddress() != null) {
            dto.setCity(unit.getAddress().getCity());
            dto.setStreetAddress(unit.getAddress().getStreetAddress());
        }

        dto.setIsAvailable(isAvailable);
        return dto;
    }
}