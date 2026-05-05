package com.ecotravel.maintenance.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Presretac koji automatski dodaje X-API-KEY i X-Trace-Id
 * na svaki odlazni Feign poziv prema accommodation servisu.
 * Programer ne mora rucno dodavati zaglavlja — sve ide automatski.
 */
@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String apiKey = request.getHeader("X-API-KEY");
            if (apiKey != null && !apiKey.isBlank()) {
                template.header("X-API-KEY", apiKey);
            }

            String traceId = request.getHeader("X-Trace-Id");
            if (traceId != null && !traceId.isBlank()) {
                template.header("X-Trace-Id", traceId);
                log.debug("Feign propagira Trace ID: {}", traceId);
            }

            String authenticatedUser = request.getHeader("X-Authenticated-User");
            if (authenticatedUser != null && !authenticatedUser.isBlank()) {
                template.header("X-Authenticated-User", authenticatedUser);
            }
        }
    }
}