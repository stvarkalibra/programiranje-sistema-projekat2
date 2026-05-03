package org.example.rezervacijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.rezervacijeservice.dto.GuestResponse;
import org.example.rezervacijeservice.exception.BadRequestException;
import org.example.rezervacijeservice.exception.ResourceNotFoundException;
import org.example.rezervacijeservice.model.Guest;
import org.example.rezervacijeservice.repository.GuestsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestsService {

    private final GuestsRepository guestsRepository;

    public List<GuestResponse> getAllGuests() {
        return guestsRepository.findAll().stream()
                .map(this::toGuestResponse)
                .collect(Collectors.toList());
    }

    public GuestResponse getGuestById(Long id) {
        Guest guest = guestsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));
        return toGuestResponse(guest);
    }

    public GuestResponse createGuest(GuestResponse dto) {
        Guest guest = new Guest();
        guest.setFirstName(dto.getFirstName());
        guest.setLastName(dto.getLastName());
        guest.setEmail(dto.getEmail());
        guest.setPhoneNumber(dto.getPhoneNumber());

        Guest saved = guestsRepository.save(guest);
        return toGuestResponse(saved);
    }

    public GuestResponse updateGuest(Long id, GuestResponse dto) {
        Guest guest = guestsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));

        if (dto.getFirstName() != null) {
            guest.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            guest.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            guest.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            guest.setPhoneNumber(dto.getPhoneNumber());
        }

        Guest updated = guestsRepository.save(guest);
        return toGuestResponse(updated);
    }

    public void deleteGuest(Long id) {
        Guest guest = guestsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + id));

        // Check if guest has bookings
        if (guest.getBookings() != null && !guest.getBookings().isEmpty()) {
            throw new BadRequestException(
                "Cannot delete guest with ID " + id + " because they have " +
                guest.getBookings().size() + " active booking(s). Please delete or transfer the bookings first."
            );
        }

        guestsRepository.deleteById(id);
    }

    public List<GuestResponse> searchGuestsByEmail(String email) {
        return guestsRepository.findAll().stream()
                .filter(g -> g.getEmail() != null && g.getEmail().contains(email))
                .map(this::toGuestResponse)
                .collect(Collectors.toList());
    }

    private GuestResponse toGuestResponse(Guest guest) {
        return new GuestResponse(
                guest.getId(),
                guest.getFirstName(),
                guest.getLastName(),
                guest.getEmail(),
                guest.getPhoneNumber()
        );
    }
}
