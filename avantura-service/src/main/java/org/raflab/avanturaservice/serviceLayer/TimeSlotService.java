package org.raflab.avanturaservice.serviceLayer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.dtoLayer.request.TimeSlotRequest;
import org.raflab.avanturaservice.dtoLayer.response.TimeSlotResponse;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.raflab.avanturaservice.repositoryLayer.AdventureRepository;
import org.raflab.avanturaservice.repositoryLayer.TimeSlotRepository;
import org.raflab.avanturaservice.utils.TimeSlotEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final AdventureRepository adventureRepository;
    private final TimeSlotEntityMapper mapper;

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        validateTimeLogic(request.getStartTime(), request.getEndTime());

        Adventure adventure = adventureRepository.findById(request.getAdventureId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avantura nije pronađena."
                ));

        TimeSlot timeSlot = mapper.toEntity(request, adventure);
        return mapper.toResponse(timeSlotRepository.save(timeSlot));
    }

    public List<TimeSlotResponse> getAllTimeSlots() {
        return timeSlotRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public TimeSlotResponse getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Termin ID " + id + " nije pronađen."
                ));
    }

    @Transactional
    public TimeSlotResponse updateTimeSlot(Long id, TimeSlotRequest request) {
        validateTimeLogic(request.getStartTime(), request.getEndTime());

        TimeSlot existing = timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Termin ne postoji."
                ));

        if (request.getMaxParticipants() < existing.getCurrentOccupancy()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Maksimalan broj učesnika ne može biti manji od trenutno prijavljenih (" + existing.getCurrentOccupancy() + ")."
            );
        }

        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setMaxParticipants(request.getMaxParticipants());

        return mapper.toResponse(timeSlotRepository.save(existing));
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        TimeSlot slot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Termin nije pronađen."
                ));

        if (slot.getCurrentOccupancy() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ne možete obrisati termin u kojem već ima prijavljenih učesnika."
            );
        }

        timeSlotRepository.deleteById(id);
    }

    private void validateTimeLogic(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vremena početka i završetka moraju biti definisana."
            );
        }

        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vreme završetka mora biti nakon vremena početka."
            );
        }

        if (java.time.Duration.between(start, end).toMinutes() < 30) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Avantura ne može trajati kraće od 30 minuta."
            );
        }
    }

    @Transactional
    public void bookSpotInTimeSlot(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Termin nije pronađen."
                ));

        if (slot.getCurrentOccupancy() >= slot.getMaxParticipants()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nema više slobodnih mesta u ovom terminu (" + slot.getMaxParticipants() + " max)."
            );
        }

        slot.setCurrentOccupancy(slot.getCurrentOccupancy() + 1);
        timeSlotRepository.save(slot);
    }

    public List<TimeSlotResponse> getAvailableTimeSlots() {
        return timeSlotRepository.findAll().stream()
                .filter(slot -> slot.getCurrentOccupancy() < slot.getMaxParticipants())
                .filter(slot -> slot.getStartTime().isAfter(LocalDateTime.now()))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}