package org.example.rezervacijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.rezervacijeservice.dto.AddOnItemResponse;
import org.example.rezervacijeservice.exception.ResourceNotFoundException;
import org.example.rezervacijeservice.model.AddOnItem;
import org.example.rezervacijeservice.repository.AddOnItemsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddOnItemsService {

    private final AddOnItemsRepository addOnItemsRepository;

    public List<AddOnItemResponse> getAddOnItemsByBookingId(Long bookingId) {
        return addOnItemsRepository.findAll().stream()
                .filter(item -> item.getBooking() != null && item.getBooking().getId().equals(bookingId))
                .map(this::toAddOnItemResponse)
                .collect(Collectors.toList());
    }

    public AddOnItemResponse getAddOnItemById(Long id) {
        AddOnItem item = addOnItemsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AddOnItem not found with id: " + id));
        return toAddOnItemResponse(item);
    }

    public AddOnItemResponse createAddOnItem(Long bookingId, AddOnItemResponse dto) {
        AddOnItem item = new AddOnItem();
        item.setServiceName(dto.getServiceName());
        item.setPrice(dto.getPrice());

        AddOnItem saved = addOnItemsRepository.save(item);
        return toAddOnItemResponse(saved);
    }

    public AddOnItemResponse updateAddOnItem(Long id, AddOnItemResponse dto) {
        AddOnItem item = addOnItemsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AddOnItem not found with id: " + id));

        if (dto.getServiceName() != null) {
            item.setServiceName(dto.getServiceName());
        }
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }

        AddOnItem updated = addOnItemsRepository.save(item);
        return toAddOnItemResponse(updated);
    }

    public void deleteAddOnItem(Long id) {
        AddOnItem item = addOnItemsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AddOnItem not found with id: " + id));
        addOnItemsRepository.deleteById(id);
    }

    public Double calculateTotalAddOnPrice(Long bookingId) {
        return getAddOnItemsByBookingId(bookingId).stream()
                .mapToDouble(AddOnItemResponse::getPrice)
                .sum();
    }

    private AddOnItemResponse toAddOnItemResponse(AddOnItem item) {
        return new AddOnItemResponse(
                item.getId(),
                item.getServiceName(),
                item.getPrice()
        );
    }
}
