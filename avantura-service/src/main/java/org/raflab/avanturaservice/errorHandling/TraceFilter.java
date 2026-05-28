package org.raflab.avanturaservice.errorHandling;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter extends OncePerRequestFilter {

    private static final String X_TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACEPARENT_HEADER = "traceparent";

    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_TRACEPARENT = "traceparent";
    private static final String MDC_SERVICE_NAME = "serviceName";
    private static final String MDC_INSTANCE_ID = "instanceId";

    @Value("${spring.application.name:avantura-service}")
    private String serviceName;

    @Value("${instance.id:${HOSTNAME:avantura-local}}")
    private String instanceId;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String traceparent = request.getHeader(TRACEPARENT_HEADER);
        String traceId = extractTraceIdFromTraceparent(traceparent);

        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader(X_TRACE_ID_HEADER);
        }

        if (traceId == null || traceId.isBlank()) {
            traceId = generateTraceId();
        }

        if (traceparent == null || traceparent.isBlank()) {
            traceparent = buildTraceparent(traceId);
        }

        MDC.put(MDC_TRACE_ID, traceId);
        MDC.put(MDC_TRACEPARENT, traceparent);
        MDC.put(MDC_SERVICE_NAME, serviceName);
        MDC.put(MDC_INSTANCE_ID, instanceId);

        response.setHeader(X_TRACE_ID_HEADER, traceId);
        response.setHeader(TRACEPARENT_HEADER, traceparent);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_TRACEPARENT);
            MDC.remove(MDC_SERVICE_NAME);
            MDC.remove(MDC_INSTANCE_ID);
        }
    }

    private String extractTraceIdFromTraceparent(String traceparent) {
        if (traceparent == null || traceparent.isBlank()) {
            return null;
        }

        try {
            String[] parts = traceparent.split("-");
            if (parts.length >= 4) {
                return parts[1];
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String buildTraceparent(String traceId) {
        return "00-" + traceId + "-" + generateSpanId() + "-01";
    }
}