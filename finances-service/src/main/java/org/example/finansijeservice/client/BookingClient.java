package org.example.finansijeservice.client;

import org.example.finansijeservice.dto.BookingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-client", url = "${BOOKING_SERVICE_URL:http://localhost:8083}/api/bookings")
public interface BookingClient {

    @GetMapping("/{id}")
    BookingDTO getBookingById(@PathVariable("id") Long id);

    @GetMapping
    List<BookingDTO> getAllBookings();

    @GetMapping("/{id}/nights")
    Long calculateNightsForBooking(@PathVariable("id") Long id);
}
