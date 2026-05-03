package org.example.rezervacijeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Guest information returned from the API")
public class GuestResponse {

    @Schema(description = "Guest ID", example = "1")
    private Long id;

    @NotBlank(message = "First name is required")
    @Schema(description = "Guest's first name", example = "Marko")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Guest's last name", example = "Petrović")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Guest's email address", example = "marko.petrovic@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Guest's phone number", example = "+381601234567")
    private String phoneNumber;
}
