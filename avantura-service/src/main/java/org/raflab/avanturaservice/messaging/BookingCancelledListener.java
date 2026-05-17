package org.raflab.avanturaservice.messaging;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.BookingCancelledEventDTO;
import org.raflab.avanturaservice.config.RabbitMQConfig;
import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.raflab.avanturaservice.repositoryLayer.TimeSlotRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingCancelledListener {

    private final TimeSlotRepository timeSlotRepository;
    private final BookingFeedbackProducer bookingFeedbackProducer;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.AVAILABILITY_QUEUE)
    public void handleBookingCancelled(BookingCancelledEventDTO event) {
        System.out.println(">>> AVANTURA primila BookingCancelledEventDTO: " + event);

        try {
            if (event.getStartDate() == null || event.getEndDate() == null) {
                String message = "NEUSPEŠNO: Booking ID " + event.getBookingId()
                        + " nije obrađen jer event nema startDate ili endDate.";

                System.out.println(">>> " + message);
                bookingFeedbackProducer.sendFeedback(message);
                return;
            }

            LocalDateTime startTime = event.getStartDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime endTime = event.getEndDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            List<TimeSlot> matchingSlots =
                    timeSlotRepository.findByStartTimeAndEndTime(startTime, endTime);

            if (matchingSlots.isEmpty()) {
                String message = "NEUSPEŠNO: Za booking ID " + event.getBookingId()
                        + " nije pronađen TimeSlot za vreme "
                        + startTime + " - " + endTime + ".";

                System.out.println(">>> " + message);
                bookingFeedbackProducer.sendFeedback(message);
                return;
            }

            int updatedCount = 0;

            for (TimeSlot slot : matchingSlots) {
                int oldOccupancy = slot.getCurrentOccupancy();

                if (oldOccupancy > 0) {
                    slot.setCurrentOccupancy(oldOccupancy - 1);
                    updatedCount++;

                    System.out.println(">>> TimeSlot ID=" + slot.getId()
                            + " occupancy smanjen sa " + oldOccupancy
                            + " na " + slot.getCurrentOccupancy());
                } else {
                    System.out.println(">>> TimeSlot ID=" + slot.getId()
                            + " već ima occupancy 0. Ne smanjujem dalje.");
                }
            }

            timeSlotRepository.saveAll(matchingSlots);

            if (updatedCount > 0) {
                String message = "USPEŠNO: Booking ID " + event.getBookingId()
                        + " je obrađen. Smanjena zauzetost za "
                        + updatedCount + " TimeSlot zapisa.";

                System.out.println(">>> " + message);
                bookingFeedbackProducer.sendFeedback(message);
            } else {
                String message = "NEUSPEŠNO: Booking ID " + event.getBookingId()
                        + " je pronašao TimeSlot zapise, ali nijedan nije imao currentOccupancy veći od 0.";

                System.out.println(">>> " + message);
                bookingFeedbackProducer.sendFeedback(message);
            }

        } catch (Exception e) {
            String message = "NEUSPEŠNO: Booking ID " + event.getBookingId()
                    + " nije obrađen zbog greške u avantura-service: "
                    + e.getMessage();

            System.out.println(">>> " + message);
            bookingFeedbackProducer.sendFeedback(message);
        }
    }
}