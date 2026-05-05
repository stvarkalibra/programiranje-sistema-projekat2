package com.ecotravel.maintenance.client;

import com.ecotravel.maintenance.dto.external.AccommodationUnitDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(
        name = "accommodation-service",
        url = "${accommodation.service.url}"
)
public interface AccommodationClient {

    /**
     * METODA 1: Dohvati sumarni prikaz smjestajne jedinice.
     * Koristi se za validaciju da li jedinica postoji prije kreiranja radnog naloga.
     */
    @GetMapping("/api/units/query/{id}/summary")
    AccommodationUnitDTO getUnitSummary(@PathVariable("id") Long unitId);

    /**
     * METODA 2: Provjeri dostupnost jedinice za odrzavanje na odredjeni datum.
     * Koristi se da osiguramo da nema booking-a u toku planiranog odrzavanja.
     */
    @GetMapping("/api/units/query/{id}/availability")
    AccommodationUnitDTO getUnitAvailability(
            @PathVariable("id") Long unitId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    /**
     * METODA 3: Dohvati sve jedinice sa sadrzajima za odredjeni grad.
     * Koristi se za planiranje odrzavanja po lokaciji.
     */
    @GetMapping("/api/units/query/by-city")
    List<AccommodationUnitDTO> getUnitsByCity(@RequestParam("city") String city);

    /**
     * METODA 4: Dohvati jedinice po tipu i maksimalnoj ukupnoj cijeni.
     * Koristi se za prioritizaciju skupih objekata pri planiranju odrzavanja.
     */
    @GetMapping("/api/units/query/by-type-and-price")
    List<AccommodationUnitDTO> getUnitsByTypeAndMaxPrice(
            @RequestParam("unitType") String unitType,
            @RequestParam("maxTotalPrice") Double maxTotalPrice
    );
}