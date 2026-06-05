package org.example.rezervacijeservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rezervacijeservice.config.RabbitMQConfig;
import org.example.rezervacijeservice.dto.BookingResponse;
import org.example.rezervacijeservice.exception.ResourceNotFoundException;
import org.example.rezervacijeservice.mapper.BookingResponseMapperImpl;
import org.example.rezervacijeservice.model.Booking;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.repository.BookingRepository;
import org.example.sharedmodule.BookingCancelledEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CancelledBookingProducer {
    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final BookingResponseMapperImpl mapper;

    public BookingResponse cancelBooking(Long id){
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.CANCELLED);
        BookingCancelledEventDTO event = new BookingCancelledEventDTO(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        Booking saved = bookingRepository.save(booking);

        log.info("Otkazana rezervacija - bookingId: {}", id);
        System.out.println("Slanje BookingCancelledEventDTO za bookingId: " + event.getBookingId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AVAILABILITY_EXCHANGE,
                RabbitMQConfig.AVAILABILITY_ROUTING_KEY,
                event
        );
        return mapper.toBookingResponse(saved);
    }

}
