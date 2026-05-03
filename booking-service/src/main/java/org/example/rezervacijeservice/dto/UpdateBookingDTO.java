package org.example.rezervacijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating a booking")
public class UpdateBookingDTO {
    @NotNull(message = "Unit ID is required")
    @Schema(description = "ID of the accommodation unit", example = "1")
    private Long unitId;

    @NotNull(message = "Guest ID is required")
    @Schema(description = "ID of the guest", example = "2")
    private Long guestId;

    @NotNull(message = "Cancellation policy ID is required")
    @Schema(description = "ID of the cancellation policy", example = "1")
    private Long cancellationPolicyId;

    @NotNull(message = "Start date is required")
    @Schema(description = "Booking start date", example = "2026-04-01")
    private Date startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Booking end date", example = "2026-04-07")
    private Date endDate;
}
