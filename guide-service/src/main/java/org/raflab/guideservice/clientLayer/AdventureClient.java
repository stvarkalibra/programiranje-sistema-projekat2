package org.raflab.guideservice.clientLayer;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.raflab.guideservice.config.FeignRequestInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "avantura-service",
        url = "http://localhost:8088/api/adventures",
        configuration = FeignRequestInterceptorConfig.class
)
public interface AdventureClient {

    @GetMapping("/guide/{guideId}/premium")
    public List<RemoteAdventureDTO> getPremium(
            @PathVariable Long guideId,
            @RequestParam Double minPrice);

    @GetMapping("/guide/{guideId}/revenue")
    public Double getRevenue(@PathVariable Long guideId);

    @GetMapping("/guide/{guideId}/check-overload")
    public Boolean checkOverload(@PathVariable Long guideId);

    @GetMapping("/category/{categoryId}")
    public List<RemoteAdventureDTO> getByCategory(@PathVariable Long categoryId);
}