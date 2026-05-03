package org.example.rezervacijeservice.mapper;

import org.example.rezervacijeservice.dto.BookingRequest;
import org.example.rezervacijeservice.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingRequestMapper {

    @Mapping(source = "guest.id", target = "guestId")
    @Mapping(source = "cancellationPolicy.id", target = "cancellationPolicyId")
    BookingRequest toBookingRequest(Booking booking);

    List<BookingRequest> toBookingRequestList(List<Booking> bookings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "cancellationPolicy", ignore = true)
    @Mapping(target = "addOnItems", ignore = true)
    @Mapping(target = "bookingStatusLogs", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    Booking toEntity(BookingRequest bookingRequest);
}
