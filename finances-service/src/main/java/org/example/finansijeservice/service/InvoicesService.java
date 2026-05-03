package org.example.finansijeservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.finansijeservice.client.BookingClient;
import org.example.finansijeservice.dto.BookingDTO;
import org.example.finansijeservice.dto.InvoiceRequest;
import org.example.finansijeservice.dto.InvoiceResponse;
import org.example.finansijeservice.dto.InvoiceUpdateRequest;
import org.example.finansijeservice.mapper.InvoiceRequestMapper;
import org.example.finansijeservice.mapper.InvoiceResponseMapper;
import org.example.finansijeservice.model.Invoice;
import org.example.finansijeservice.model.InvoiceStatus;
import org.example.finansijeservice.model.TaxRate;
import org.example.finansijeservice.repository.InvoicesRepository;
import org.example.finansijeservice.repository.TaxRatesRepository;
import org.example.finansijeservice.exception.BadRequestException;
import org.example.finansijeservice.exception.RateLimitException;
import org.example.finansijeservice.exception.ResourceNotFoundException;
import org.example.finansijeservice.strategy.TaxCalculationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicesService {

    private final InvoicesRepository invoicesRepository;
    private final InvoiceRequestMapper requestMapper;
    private final InvoiceResponseMapper responseMapper;
    private final TaxRatesRepository taxRatesRepository;
    private final TaxCalculationStrategy taxCalculationStrategy;
    private final BookingClient bookingClient;

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoicesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return responseMapper.toInvoiceResponse(invoice);
    }


    @CircuitBreaker(name = "bookingServiceCB", fallbackMethod = "fallbackCreateInvoice")
    @RateLimiter(name = "basicLimit", fallbackMethod = "fallbackRateLimit")
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest){
        BookingDTO booking = bookingClient.getBookingById(invoiceRequest.getBookingId());

        validateBooking(booking);
        validateBookingAmount(booking);
        validateNoActiveInvoiceForBooking(invoiceRequest.getBookingId());

        Invoice invoice = requestMapper.toEntity(invoiceRequest);
        invoice.setSubtotal(booking.getTotalPrice());
        TaxRate taxRate = taxRatesRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TaxRate not found with id: " + 1
                ));
        invoice.setTaxRate(taxRate);
        invoice.setTotalAmount(taxCalculationStrategy.calculate(invoice.getSubtotal(), taxRate.getPercentage()));

        return responseMapper.toInvoiceResponse(invoicesRepository.save(invoice));
    }

    public InvoiceResponse fallbackRateLimit(InvoiceRequest request, Throwable t) {
        throw new RateLimitException("Previše zahteva! Molimo sačekajte.", HttpStatus.TOO_MANY_REQUESTS);
    }

    public InvoiceResponse fallbackCreateInvoice(InvoiceRequest dto, Throwable t) {
        if (t instanceof ResourceNotFoundException) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Rezervacija sa ID-jem " + dto.getBookingId() + " nije pronađena."
            );
        }

        if (t instanceof ResponseStatusException) {
            throw (ResponseStatusException) t;
        }

        // Pravi pad sistema (Connection Refused, Timeout, ili je CB već OPEN)
        // Kada stvarno možemo reći da servis nije dostupan
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Sistem za rezervacije je trenutno nedostupan ili preopterećen."
        );
    }

    private void validateBooking(BookingDTO booking) {
        List<String> validStatuses = List.of("CONFIRMED", "COMPLETED");
        if (!validStatuses.contains(booking.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nije moguće kreirati račun za rezervaciju sa statusom: " + booking.getStatus() +
                            ". Rezervacija mora biti u statusu CONFIRMED ili COMPLETED."
            );
        }
    }

    private void validateNoActiveInvoiceForBooking(Long bookingId) {
        List<Invoice> existing = invoicesRepository.findByBookingId(bookingId);
        boolean hasActive = existing.stream()
                .anyMatch(i -> !InvoiceStatus.CANCELLED.equals(i.getStatus()));
        if (hasActive) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Aktivna faktura za rezervaciju sa ID-jem " + bookingId + " već postoji."
            );
        }
    }

    private void validateBookingAmount(BookingDTO booking) {
        if (booking.getTotalPrice() == null || booking.getTotalPrice() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rezervacija nema validan iznos."
            );
        }
    }

    public List<InvoiceResponse> getInvoices(String status, Long bookingId) {
        InvoiceStatus invoiceStatus = status != null ? InvoiceStatus.valueOf(status) : null;

        if (invoiceStatus != null && bookingId != null) {
            return responseMapper.toInvoiceResponseList(invoicesRepository.findByStatusAndBookingId(invoiceStatus, bookingId));
        } else if (invoiceStatus != null) {
            return responseMapper.toInvoiceResponseList(invoicesRepository.findByStatus(invoiceStatus));
        } else if (bookingId != null) {
            return responseMapper.toInvoiceResponseList(invoicesRepository.findByBookingId(bookingId));
        } else {
            return responseMapper.toInvoiceResponseList(invoicesRepository.findAll());
        }
    }


    public InvoiceResponse updateInvoice(Long id, InvoiceUpdateRequest request) {

        Invoice invoice = invoicesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + id
                ));

        validateStatusTransition(invoice.getStatus());

        responseMapper.updateInvoiceFromRequest(request, invoice);

        Invoice updated = invoicesRepository.save(invoice);
        return responseMapper.toInvoiceResponse(updated);
    }

    private void validateStatusTransition(InvoiceStatus current) {
        if (InvoiceStatus.PAID.equals(current)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nije moguće mijenjati račun koji je već plaćen."
            );
        }
        if (InvoiceStatus.CANCELLED.equals(current)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nije moguće mijenjati otkazani račun."
            );
        }
    }


    public List<InvoiceResponse> getPartiallyPaidInvoices() {
        return responseMapper.toInvoiceResponseList(invoicesRepository.findPartiallyPaidInvoices());
    }

    public List<InvoiceResponse> getInvoicesByTaxNameAndMinAmount(String taxName, Double minAmount) {
        return responseMapper.toInvoiceResponseList(
                invoicesRepository.findByTaxNameAndMinAmount(taxName, minAmount)
        );
    }

    @CircuitBreaker(name = "bookingServiceCB", fallbackMethod = "fallbackGetAllBookings")
    public List<BookingDTO> getAllBookings() {
        return bookingClient.getAllBookings();
    }

    public List<BookingDTO> fallbackGetAllBookings(Throwable t) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Nije moguće dohvatiti rezervacije: sistem je trenutno nedostupan."
        );
    }

    @CircuitBreaker(name = "bookingServiceCB", fallbackMethod = "fallbackGetBookingNights")
    public Long getBookingNights(Long bookingId) {
        return bookingClient.calculateNightsForBooking(bookingId);
    }

    public Long fallbackGetBookingNights(Long bookingId, Throwable t) {
        if (t instanceof feign.FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Rezervacija sa ID-jem " + bookingId + " nije pronađena."
            );
        }
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Nije moguće izračunati broj noći: sistem je trenutno nedostupan."
        );
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoicesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + id
                ));
        if (InvoiceStatus.PAID.equals(invoice.getStatus())) {
            throw new BadRequestException("Cannot delete a paid invoice");
        }

        invoicesRepository.deleteById(id);
    }

}
