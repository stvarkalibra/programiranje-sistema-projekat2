package org.example.rezervacijeservice.mapper;

import org.example.rezervacijeservice.dto.BookingResponse;
import org.example.rezervacijeservice.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingResponseMapper {

    BookingResponse toBookingResponse(Booking booking);

    List<BookingResponse> toBookingResponseList(List<Booking> bookings);

    @Mapping(target = "bookingStatusLogs", ignore = true)
    Booking toEntity(BookingResponse bookingResponse);
}
