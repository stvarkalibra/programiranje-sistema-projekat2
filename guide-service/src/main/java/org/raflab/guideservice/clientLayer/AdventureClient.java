package org.raflab.guideservice.clientLayer;

import org.raflab.guideservice.config.FeignRequestInterceptorConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "avantura-service",
        url = "${AVANTURA_SERVICE_URL:http://localhost:8088}",
        configuration = FeignRequestInterceptorConfig.class
)
public interface AdventureClient {

    @GetMapping("/api/adventures/guide/{guideId}/premium")
    List<RemoteAdventureDTO> getPremium(
            @PathVariable("guideId") Long guideId,
            @RequestParam("minPrice") Double minPrice
    );

    @GetMapping("/api/adventures/guide/{guideId}/revenue")
    Double getRevenue(@PathVariable("guideId") Long guideId);

    @GetMapping("/api/adventures/guide/{guideId}/check-overload")
    Boolean checkOverload(@PathVariable("guideId") Long guideId);

    @GetMapping("/api/adventures/category/{categoryId}")
    List<RemoteAdventureDTO> getByCategory(@PathVariable("categoryId") Long categoryId);
}