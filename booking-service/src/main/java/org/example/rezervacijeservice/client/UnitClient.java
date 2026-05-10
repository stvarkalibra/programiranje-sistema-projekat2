package org.example.rezervacijeservice.client;

import org.example.rezervacijeservice.dto.UnitDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "smestaj-service", url = "${ACCOMMODATION_SERVICE_URL:http://localhost:8081}")

public interface UnitClient {
    @GetMapping("/api/units/{id}")
    UnitDTO getUnitById(@PathVariable("id") Long id);
}
