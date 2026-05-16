package org.example.sharedmodule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEventDTO {
    private Long bookingId;
    private Date startDate;
    private Date endDate;
}
