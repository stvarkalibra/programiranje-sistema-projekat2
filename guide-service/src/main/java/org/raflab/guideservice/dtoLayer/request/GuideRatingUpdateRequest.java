package org.raflab.guideservice.dtoLayer.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideRatingUpdateRequest {

    @NotNull(message = "Novi rating je obavezan.")
    @DecimalMin(value = "0.0", message = "Rating ne može biti manji od 0.")
    @DecimalMax(value = "5.0", message = "Rating ne može biti veći od 5.")
    private Double newRating;
}