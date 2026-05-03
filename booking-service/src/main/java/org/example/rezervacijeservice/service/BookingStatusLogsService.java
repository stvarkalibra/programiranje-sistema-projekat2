package org.example.rezervacijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.rezervacijeservice.model.BookingStatus;
import org.example.rezervacijeservice.model.BookingStatusLog;
import org.example.rezervacijeservice.repository.BookingStatusLogsRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingStatusLogsService {

    private final BookingStatusLogsRepository bookingStatusLogsRepository;

    public void logStatusChange(Long bookingId, BookingStatus oldStatus, BookingStatus newStatus) {
        BookingStatusLog log = new BookingStatusLog();
        log.setOldStatus(oldStatus != null ? oldStatus.toString() : null);
        log.setNewStatus(newStatus.toString());
        log.setChangedDate(new Date());

        bookingStatusLogsRepository.save(log);
    }

    public List<BookingStatusLog> getStatusHistoryForBooking(Long bookingId) {
        return bookingStatusLogsRepository.findAll().stream()
                .filter(log -> log.getBooking() != null && log.getBooking().getId().equals(bookingId))
                .sorted((a, b) -> b.getChangedDate().compareTo(a.getChangedDate()))
                .collect(Collectors.toList());
    }

    public List<BookingStatusLog> getAllStatusLogs() {
        return bookingStatusLogsRepository.findAll().stream()
                .sorted((a, b) -> b.getChangedDate().compareTo(a.getChangedDate()))
                .collect(Collectors.toList());
    }
}
