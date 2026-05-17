package com.ecotravel.accommodation.service;

import com.ecotravel.accommodation.dto.request.AddressRequestDTO;
import com.ecotravel.accommodation.dto.request.UnitRequestDTO;
import com.ecotravel.accommodation.dto.response.*;
import com.ecotravel.accommodation.messaging.UnitEventPublisher;
import com.ecotravel.accommodation.model.*;
import com.ecotravel.accommodation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final FacilityRepository facilityRepository;
    private final PriceTierRepository priceTierRepository;
    private final AvailabilityCalendarRepository availabilityCalendarRepository;
    private final FacilityService facilityService;
    private final UnitEventPublisher unitEventPublisher;  // << NOVO

    public List<UnitResponseDTO> getAll() {
        return unitRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UnitResponseDTO getById(Long id) {
        Unit unit = findUnitById(id);
        return convertToDTO(unit);
    }

    public List<UnitResponseDTO> getByType(String unitType) {
        return unitRepository.findByUnitType(unitType.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UnitResponseDTO> getByCity(String city) {
        return unitRepository.findByAddress_City(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UnitResponseDTO> getByMaxPrice(Double maxPrice) {
        return unitRepository.findByBasePricePerNightLessThanEqual(maxPrice)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Kreira novu smještajnu jedinicu i publishuje UnitCreatedEvent.
     * Maintenance servis sluša i loguje novu jedinicu u svom sistemu.
     */
    public UnitResponseDTO create(UnitRequestDTO dto) {
        Unit unit = new Unit();
        unit.setName(dto.getName());
        unit.setUnitType(dto.getUnitType().toUpperCase());
        unit.setBasePricePerNight(dto.getBasePricePerNight());

        if (dto.getAddress() != null) {
            Address address = mapAddress(dto.getAddress());
            unit.setAddress(address);
        }

        if (dto.getFacilityIds() != null && !dto.getFacilityIds().isEmpty()) {
            List<Facility> facilities = facilityRepository.findAllById(dto.getFacilityIds());
            unit.setFacilities(facilities);
        }

        Unit saved = unitRepository.save(unit);

        // Publishuj asinhroni događaj NAKON što je save() uspio
        String city = (saved.getAddress() != null) ? saved.getAddress().getCity() : null;
        unitEventPublisher.publishUnitCreated(
                saved.getId(),
                saved.getName(),
                saved.getUnitType(),
                city,
                saved.getBasePricePerNight()
        );

        return convertToDTO(saved);
    }

    public UnitResponseDTO update(Long id, UnitRequestDTO dto) {
        Unit unit = findUnitById(id);
        unit.setName(dto.getName());
        unit.setUnitType(dto.getUnitType().toUpperCase());
        unit.setBasePricePerNight(dto.getBasePricePerNight());

        if (dto.getAddress() != null) {
            Address address = unit.getAddress() != null ? unit.getAddress() : new Address();
            address.setCity(dto.getAddress().getCity());
            address.setZipCode(dto.getAddress().getZipCode());
            address.setStreetAddress(dto.getAddress().getStreetAddress());
            unit.setAddress(address);
        }

        if (dto.getFacilityIds() != null) {
            List<Facility> facilities = facilityRepository.findAllById(dto.getFacilityIds());
            unit.setFacilities(facilities);
        }

        return convertToDTO(unitRepository.save(unit));
    }

    /**
     * Briše smještajnu jedinicu i publishuje UnitDeletedEvent.
     * Maintenance servis sluša i automatski otkazuje sve PENDING naloge za tu jedinicu.
     */
    public void delete(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Smjestajna jedinica sa ID " + id + " nije pronadjena"));

        String unitName = unit.getName(); // sačuvaj prije brisanja

        unitRepository.deleteById(id);

        // Publishuj događaj NAKON uspješnog brisanja
        unitEventPublisher.publishUnitDeleted(id, unitName);
    }

    /**
     * Matematička kalkulacija: ukupna cijena za period.
     * Uzima u obzir: broj noćenja, sezonski množač i extra_cost sadržaja.
     */
    public PriceCalculationResponseDTO calculatePrice(Long unitId, LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Datum dolaska mora biti prije datuma odlaska");
        }

        Unit unit = findUnitById(unitId);
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);

        double facilitiesExtraCost = 0.0;
        if (unit.getFacilities() != null) {
            facilitiesExtraCost = unit.getFacilities()
                    .stream()
                    .mapToDouble(Facility::getExtraCost)
                    .sum();
        }

        double seasonalMultiplier = 1.0;
        String activeSeason = null;
        List<PriceTier> priceTiers = priceTierRepository.findByUnit_Id(unitId);
        for (PriceTier tier : priceTiers) {
            if (!checkIn.isBefore(tier.getStartDate()) && !checkIn.isAfter(tier.getEndDate())) {
                seasonalMultiplier = tier.getMultiplier();
                activeSeason = tier.getName();
                break;
            }
        }

        double pricePerNight = unit.getBasePricePerNight() + facilitiesExtraCost;
        double totalPrice = pricePerNight * seasonalMultiplier * numberOfNights;

        PriceCalculationResponseDTO result = new PriceCalculationResponseDTO();
        result.setUnitId(unitId);
        result.setUnitName(unit.getName());
        result.setCheckIn(checkIn);
        result.setCheckOut(checkOut);
        result.setNumberOfNights((int) numberOfNights);
        result.setBasePricePerNight(unit.getBasePricePerNight());
        result.setFacilitiesExtraCost(facilitiesExtraCost);
        result.setSeasonalMultiplier(seasonalMultiplier);
        result.setActiveSeason(activeSeason);
        result.setTotalPrice(Math.round(totalPrice * 100.0) / 100.0);

        return result;
    }

    private Unit findUnitById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Smjestajna jedinica sa ID " + id + " nije pronadjena"));
    }

    private Address mapAddress(AddressRequestDTO dto) {
        Address address = new Address();
        address.setCity(dto.getCity());
        address.setZipCode(dto.getZipCode());
        address.setStreetAddress(dto.getStreetAddress());
        return address;
    }

    public UnitResponseDTO convertToDTO(Unit unit) {
        UnitResponseDTO dto = new UnitResponseDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setUnitType(unit.getUnitType());
        dto.setBasePricePerNight(unit.getBasePricePerNight());

        double facilitiesExtra = 0.0;
        List<FacilityResponseDTO> facilityDTOs = new ArrayList<>();
        if (unit.getFacilities() != null) {
            for (Facility f : unit.getFacilities()) {
                facilitiesExtra += f.getExtraCost();
                facilityDTOs.add(facilityService.convertToDTO(f));
            }
        }
        dto.setTotalPriceWithFacilities(unit.getBasePricePerNight() + facilitiesExtra);
        dto.setFacilities(facilityDTOs);

        if (unit.getAddress() != null) {
            AddressResponseDTO addressDTO = new AddressResponseDTO();
            addressDTO.setId(unit.getAddress().getId());
            addressDTO.setCity(unit.getAddress().getCity());
            addressDTO.setZipCode(unit.getAddress().getZipCode());
            addressDTO.setStreetAddress(unit.getAddress().getStreetAddress());
            dto.setAddress(addressDTO);
        }

        return dto;
    }
}