package org.example.finansijeservice.service;
import lombok.AllArgsConstructor;
import org.example.finansijeservice.config.RabbitMQConfig;
import org.example.finansijeservice.dto.TransactionDTO;
import org.example.finansijeservice.exception.ResourceNotFoundException;
import org.example.finansijeservice.factory.PaymentProcessor;
import org.example.finansijeservice.factory.PaymentProcessorFactory;
import org.example.finansijeservice.mapper.TransactionDTOMapper;
import org.example.finansijeservice.model.Invoice;
import org.example.finansijeservice.model.PaymentMethod;
import org.example.finansijeservice.model.Transaction;
import org.example.finansijeservice.repository.InvoicesRepository;
import org.example.finansijeservice.repository.PaymentMethodsRepository;
import org.example.finansijeservice.repository.TransactionsRepository;
import org.example.sharedmodule.PaymentCompletedEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TransactionsRepository transactionsRepository;
    private final PaymentMethodsRepository paymentMethodsRepository;
    private final InvoicesRepository invoicesRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final TransactionDTOMapper mapper;

    public TransactionDTO createAndPublish(TransactionDTO request) {
        PaymentMethod paymentMethod = paymentMethodsRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment method not found with id: " + request.getPaymentMethodId()));

        Invoice invoice = invoicesRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        PaymentProcessor processor = paymentProcessorFactory.create(paymentMethod);
        Transaction tx = mapper.toEntity(request);
        tx.setPaymentMethod(paymentMethod);
        tx.setInvoice(invoice);
        processor.process(tx);

        Transaction saved = transactionsRepository.save(tx);

        PaymentCompletedEventDTO event = new PaymentCompletedEventDTO(
                saved.getId(),
                saved.getInvoice().getBookingId(),
                saved.getAmount()
        );

        System.out.println("Slanje PaymentCompletedEvent za bookingId: " + event.getBookingId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        return mapper.toTransactionDTO(saved);
    }
}
