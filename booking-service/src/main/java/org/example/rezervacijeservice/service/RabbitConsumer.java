package org.example.rezervacijeservice.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.rezervacijeservice.config.RabbitMQConfig;
import org.example.rezervacijeservice.model.Booking;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.repository.BookingRepository;
import org.example.sharedmodule.PaymentCompletedEventDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitConsumer {
//
//    @RabbitListener(queues = RabbitMQConfig.QUEUE)
//    public void consume(String message) {
//        System.out.println("LOG IZ SERVISA B: Primljena poruka -> " + message);
//    }

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleOrder(Message message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PaymentCompletedEventDTO event = mapper.readValue(message.getBody(), PaymentCompletedEventDTO.class);
            Booking booking = bookingRepository.findById(event.getBookingId()).get();
            if(booking != null){
                booking.setStatus(BookingStatus.COMPLETED);
            } else {
                System.out.println("Booking ID je NULL");
            }
            bookingRepository.save(booking);
            System.out.println("USPEŠNO PRIMLJEN EVENT ZA TRANSACTION: " + event);
            rabbitTemplate.convertAndSend("transaction_feedback_exchange", "feedback_key", "Updated Booking status");
            System.out.println("Feedback poslat za transakciju: " + event.getTransactionId());
        } catch (Exception e) {
            System.out.println("Greska pri obradi transakcije: " + e.getMessage());
            rabbitTemplate.convertAndSend("transaction_feedback_exchange", "feedback_key", "Greska pri promeni statusa rezervacije");
        }
    }


}
