package org.raflab.avanturaservice.dtoLayer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// dtoLayer/request/AdventureCategoryRequest.java
@Data
public class AdventureCategoryRequest {
    @NotBlank(message = "Ime kategorije je obavezno")
    @Size(max = 50, message = "Ime ne može biti duže od 50 karaktera")
    private String name;

    @NotBlank(message = "Opis je obavezan")
    private String description;
}
