package org.example.rezervacijeservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.example.rezervacijeservice.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rezervacijeservice.client.UnitClient;
import org.example.rezervacijeservice.dto.*;
import org.example.rezervacijeservice.mapper.BookingRequestMapper;
import org.example.rezervacijeservice.mapper.BookingResponseMapper;
import org.example.rezervacijeservice.model.Booking;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.model.CancellationPolicy;
import org.example.rezervacijeservice.model.Guest;
import org.example.rezervacijeservice.repository.BookingRepository;
import org.example.rezervacijeservice.repository.CancellationPoliciesRepository;
import org.example.rezervacijeservice.repository.GuestsRepository;
import org.example.rezervacijeservice.exception.RateLimitException;
import org.example.rezervacijeservice.exception.ResourceNotFoundException;
import org.example.rezervacijeservice.strategy.CancellationRefundStrategy;
import org.example.rezervacijeservice.strategy.CancellationStrategyResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingRequestMapper requestMapper;
    private final BookingResponseMapper responseMapper;
    private final BookingStatusLogsService bookingStatusLogsService;
    private final AddOnItemsService addOnItemsService;
    private final CancellationStrategyResolver cancellationStrategyResolver;
    private final UnitClient unitClient;
    private final GuestsRepository guestsRepository;
    private final CancellationPoliciesRepository cancellationPoliciesRepository;

    public List<BookingResponse> getAllBookings() {
        return responseMapper.toBookingResponseList(bookingRepository.findAll());
    }

    public BookingResponse getBookingById(Long id) {
        log.info("Pretraga rezervacije - bookingId: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return responseMapper.toBookingResponse(booking);
    }

    @RateLimiter(name = "basicLimit", fallbackMethod = "fallbackRateLimit")
    @CircuitBreaker(name = "smestajServiceCB", fallbackMethod = "fallbackCreateBooking")
    public BookingResponse createBooking(BookingRequest request) {
        Booking booking = requestMapper.toEntity(request);
        booking.setStatus(BookingStatus.PENDING);

        Guest guest = guestsRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + request.getGuestId()));
        booking.setGuest(guest);

        CancellationPolicy policy = cancellationPoliciesRepository.findById(request.getCancellationPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("CancellationPolicy not found with id: " + request.getCancellationPolicyId()));
        booking.setCancellationPolicy(policy);

        //Double addOnTotalPrice = addOnItemsService.calculateTotalAddOnPrice(booking.getId());/// Booking id je null jer se jos nije kreirao
        UnitDTO unit = unitClient.getUnitById(request.getUnitId());

        LocalDate startLocal = request.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocal = request.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        long numberOfNights = ChronoUnit.DAYS.between(startLocal, endLocal);

        double facilitiesTotal = unit.getFacilities().stream()
                .mapToDouble(FacilityDTO::getExtraCost)
                .sum();

        booking.setTotalPrice((unit.getBasePrice() + facilitiesTotal) * numberOfNights);


        Booking saved = bookingRepository.save(booking);

        bookingStatusLogsService.logStatusChange(saved.getId(), null, BookingStatus.PENDING);

        return responseMapper.toBookingResponse(saved);
    }

    public BookingResponse fallbackRateLimit(BookingRequest request, Throwable t) {
        throw new RateLimitException("Previše zahteva! Molimo sačekajte.", HttpStatus.TOO_MANY_REQUESTS);
    }

    public BookingResponse fallbackCreateBooking(BookingRequest dto, Throwable t) {
        if (t instanceof ResourceNotFoundException) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Smestaj sa ID-jem " + dto.getUnitId() + " nije pronađen."
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

    public BookingResponse updateBooking(Long id, UpdateBookingDTO request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        BookingStatus oldStatus = booking.getStatus();

        if (request.getUnitId() != null) {
            booking.setUnitId(request.getUnitId());
        }
        if (request.getStartDate() != null) {
            booking.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            booking.setEndDate(request.getEndDate());
        }
        if (request.getCancellationPolicyId() != null) {
            // Would load from repository in real app
        }
        if (request.getGuestId() != null) {
            // Would load from repository in real app
        }

        Booking updated = bookingRepository.save(booking);
        return responseMapper.toBookingResponse(updated);
    }

    public BookingResponse updateBookingStatus(Long id, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a cancelled booking");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(newStatus);

        Booking updated = bookingRepository.save(booking);

        bookingStatusLogsService.logStatusChange(id, oldStatus, newStatus);

        return responseMapper.toBookingResponse(updated);
    }

//    public void deleteBooking(Long id) {
//        Booking booking = bookingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
//        bookingRepository.deleteById(id);
//    }

    public List<BookingResponse> getBookingsByUnitId(Long unitId) {
        return responseMapper.toBookingResponseList(
            bookingRepository.findAll().stream()
                .filter(b -> unitId.equals(b.getUnitId()))
                .collect(Collectors.toList())
        );
    }

    public List<BookingResponse> getBookingsByDate(LocalDate date) {
        return responseMapper.toBookingResponseList(
            bookingRepository.findAll().stream()
                .filter(b -> isBookedOnDate(b, date))
                .collect(Collectors.toList())
        );
    }

    public List<BookingResponse> getBookingsByUnitIdAndDate(Long unitId, LocalDate date) {
        return responseMapper.toBookingResponseList(
            bookingRepository.findAll().stream()
                .filter(b -> unitId.equals(b.getUnitId()) && isBookedOnDate(b, date))
                .collect(Collectors.toList())
        );
    }

    private boolean isBookedOnDate(Booking b, LocalDate date) {
        if (b.getStartDate() == null || b.getEndDate() == null) return false;
        LocalDate start = b.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end   = b.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return !date.isBefore(start) && date.isBefore(end);
    }

    public double calculateCancellationRefund(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        CancellationRefundStrategy strategy =
                cancellationStrategyResolver.resolve(booking.getCancellationPolicy());
        return strategy.calculateRefund(booking.getTotalPrice());
    }

    public Long calculateNightsStay(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        LocalDate startLocal = booking.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocal = booking.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        return java.time.temporal.ChronoUnit.DAYS.between(startLocal, endLocal);
    }
}
