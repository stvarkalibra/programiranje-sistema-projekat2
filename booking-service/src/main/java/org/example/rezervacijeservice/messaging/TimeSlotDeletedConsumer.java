package org.example.rezervacijeservice.messaging;

import lombok.RequiredArgsConstructor;
import org.example.rezervacijeservice.config.RabbitMQConfig;
import org.example.rezervacijeservice.model.Booking;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.repository.BookingRepository;
import org.example.sharedmodule.TimeSlotDeletedEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotDeletedConsumer {

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.TIME_SLOT_DELETED_QUEUE)
    public void handleTimeSlotDeleted(TimeSlotDeletedEventDTO event) {
        try {
            System.out.println(">>> BOOKING PRIMIO TimeSlotDeletedEventDTO: " + event);

            Date startDate = Date.from(event.getStartTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            Date endDate = Date.from(event.getEndTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            List<Booking> bookings = bookingRepository
                    .findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);

            if (bookings.isEmpty()) {
                String feedback = "NEUSPEŠNO: Nema rezervacija za obrisani TimeSlot ID="
                        + event.getTimeSlotId();

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.BOOKING_FEEDBACK_EXCHANGE,
                        RabbitMQConfig.BOOKING_FEEDBACK_KEY,
                        feedback
                );

                System.out.println(">>> BOOKING POSLAO FEEDBACK AVANTURI: " + feedback);
                return;
            }

            for (Booking booking : bookings) {
                booking.setStatus(BookingStatus.CANCELLED);
            }

            bookingRepository.saveAll(bookings);

            String feedback = "USPEŠNO: Otkazano "
                    + bookings.size()
                    + " rezervacija za obrisani TimeSlot ID="
                    + event.getTimeSlotId();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_FEEDBACK_EXCHANGE,
                    RabbitMQConfig.BOOKING_FEEDBACK_KEY,
                    feedback
            );

            System.out.println(">>> BOOKING POSLAO FEEDBACK AVANTURI: " + feedback);

        } catch (Exception e) {
            String feedback = "NEUSPEŠNO: Greška pri obradi TimeSlotDeletedEventDTO, timeSlotId="
                    + event.getTimeSlotId()
                    + ", razlog: " + e.getMessage();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_FEEDBACK_EXCHANGE,
                    RabbitMQConfig.BOOKING_FEEDBACK_KEY,
                    feedback
            );

            System.out.println(">>> BOOKING POSLAO ERROR FEEDBACK AVANTURI: " + feedback);

            throw e;
        }
    }
}