package org.example.rezervacijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.rezervacijeservice.model.BookingStatus;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Booking details returned from the API")
public class BookingResponse {

    @Schema(description = "Booking ID", example = "1")
    private Long id;

    @Schema(description = "ID of the accommodation unit", example = "3")
    private Long unitId;

    @Schema(description = "Guest information")
    private GuestResponse guest;

    @Schema(description = "Applied cancellation policy")
    private CancellationPolicyResponse cancellationPolicy;

    @Schema(description = "List of add-on services included in the booking")
    private List<AddOnItemResponse> addOnItems;

    @Schema(description = "Booking start date", example = "2026-04-01")
    private Date startDate;

    @Schema(description = "Booking end date", example = "2026-04-07")
    private Date endDate;

    @Schema(description = "Total price of the booking including add-ons", example = "350.00")
    private Double totalPrice;

    @Schema(description = "Current booking status", example = "CONFIRMED",
            allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"})
    private BookingStatus status;
}
