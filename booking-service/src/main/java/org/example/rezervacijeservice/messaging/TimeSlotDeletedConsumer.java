package org.example.rezervacijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.rezervacijeservice.config.RabbitMQConfig;
import org.example.rezervacijeservice.model.Booking;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.repository.BookingRepository;
import org.example.sharedmodule.TimeSlotDeletedEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotDeletedConsumer {

    private final BookingRepository bookingRepository;

    @RabbitListener(queues = RabbitMQConfig.TIME_SLOT_DELETED_QUEUE)
    @Transactional
    public void handleTimeSlotDeleted(TimeSlotDeletedEventDTO event) {
        System.out.println(">>> BOOKING PRIMIO EVENT: TimeSlotDeletedEventDTO, timeSlotId="
                + event.getTimeSlotId()
                + ", startTime=" + event.getStartTime()
                + ", endTime=" + event.getEndTime());

        Date startDate = Date.from(
                event.getStartTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        Date endDate = Date.from(
                event.getEndTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        List<Booking> bookings = bookingRepository
                .findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        bookingRepository.saveAll(bookings);

        System.out.println(">>> BOOKING OTKAZAO REZERVACIJA: "
                + bookings.size()
                + " za period "
                + event.getStartTime()
                + " - "
                + event.getEndTime());
    }
}