package org.example.sharedmodule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEventDTO {
    private Long bookingId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
