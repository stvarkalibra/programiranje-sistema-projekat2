package com.example.gatewayservice.filters;

import com.example.gatewayservice.dto.AuthResponseDTO;
import com.example.gatewayservice.exeptions.InvalidApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_TRACE_ID = "X-Trace-Id";
    private static final String TRACEPARENT = "traceparent";
    private static final String X_AUTHENTICATED_USER = "X-Authenticated-User";
    private static final String X_USER_ROLE = "X-User-Role";

    private final WebClient webClient;

    public AuthFilter(@Value("${AUTH_SERVICE_URL:http://auth-service:8085}") String authServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    private static final List<String> EXCLUDED_PATHS = List.of(
            "v3/api-docs",
            "swagger-ui",
            "swagger-config",
            "webjars",
            "actuator"
    );

    private static final List<String> PROTECTED_KEYWORDS = List.of(
            "/api/booking",
            "/api/add-on-items",
            "/api/booking-status-logs",
            "/api/cancellation-policies",
            "/api/guests",
            "/api/invoices",
            "/api/payment-methods",
            "/api/tax-rates",
            "/api/transactions",

            "/api/guides",
            "/api/languages",
            "/api/guide-languages",
            "/api/certifications",

            "/api/adventures",
            "/api/reviews",
            "/api/categories",
            "/api/gear",
            "/api/time-slots"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();

        String existingTraceparent = exchange.getRequest().getHeaders().getFirst(TRACEPARENT);

        String traceId;
        String traceparent;

        if (existingTraceparent != null && !existingTraceparent.isBlank()) {
            traceparent = existingTraceparent;
            traceId = extractTraceIdFromTraceparent(existingTraceparent);
        } else {
            traceId = generateTraceId();
            traceparent = buildTraceparent(traceId);
        }

        log.info("[traceId={}] [traceparent={}] GATEWAY: [{}] {}", traceId, traceparent, method, path);

        if (EXCLUDED_PATHS.stream().anyMatch(path::contains)) {
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(X_TRACE_ID, traceId)
                    .header(TRACEPARENT, traceparent)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(X_API_KEY);

        if (apiKey == null || apiKey.isBlank()) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "X-API-KEY fali!", traceId, traceparent);
        }

        return webClient.get()
                .uri("/api/auth/validate")
                .header(X_API_KEY, apiKey)
                .header(X_TRACE_ID, traceId)
                .header(TRACEPARENT, traceparent)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Nevalidan kljuc")
                                .flatMap(body -> Mono.error(new InvalidApiKeyException("Nevalidan kljuc")))
                )
                .bodyToMono(AuthResponseDTO.class)
                .flatMap(auth -> {
                    if (!auth.isValid()) {
                        return onError(exchange, HttpStatus.FORBIDDEN, "Nevalidan kljuc", traceId, traceparent);
                    }

                    List<String> roles = auth.getRoles();
                    boolean isProtected = PROTECTED_KEYWORDS.stream().anyMatch(path::contains);

                    if (isProtected) {
                        if (method.equals("DELETE") && !roles.contains("ADMIN")) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Samo admin brise", traceId, traceparent);
                        }

                        if ((method.equals("POST") || method.equals("PUT")) &&
                                (!roles.contains("ADMIN") && !roles.contains("GUIDE"))) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Guest ne moze da kreira i menja", traceId, traceparent);
                        }
                    }

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header(X_USER_ROLE, String.join(",", roles))
                            .header(X_AUTHENTICATED_USER, auth.getUsername())
                            .header(X_TRACE_ID, traceId)
                            .header(TRACEPARENT, traceparent)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build())
                            .doOnSubscribe(sub -> exchange.getResponse().beforeCommit(() -> {
                                exchange.getResponse().getHeaders()
                                        .add("X-Processing-Time", (System.currentTimeMillis() - startTime) + "ms");
                                exchange.getResponse().getHeaders()
                                        .add(X_TRACE_ID, traceId);
                                exchange.getResponse().getHeaders()
                                        .add(TRACEPARENT, traceparent);
                                exchange.getResponse().getHeaders()
                                        .add("X-Eco-Service-Status", "ECO-TOURISM-OK");
                                exchange.getResponse().getHeaders()
                                        .add("X-Served-By", "Gateway-v1.0");
                                return Mono.empty();
                            }));
                })
                .onErrorResume(e -> {
                    if (e instanceof InvalidApiKeyException) {
                        return onError(exchange, HttpStatus.FORBIDDEN, e.getMessage(), traceId, traceparent);
                    }

                    return onError(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Auth servis nedostupan", traceId, traceparent);
                });
    }

    private Mono<Void> onError(
            ServerWebExchange exchange,
            HttpStatus status,
            String message,
            String traceId,
            String traceparent
    ) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add(X_TRACE_ID, traceId);
        response.getHeaders().add(TRACEPARENT, traceparent);

        String timestamp = LocalDateTime.now().toString();

        String body = String.format(
                "{\"timestamp\": \"%s\", \"message\": \"%s\", \"traceId\": \"%s\", \"traceparent\": \"%s\", \"status\": %d}",
                timestamp,
                message,
                traceId,
                traceparent,
                status.value()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
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

    private String extractTraceIdFromTraceparent(String traceparent) {
        try {
            String[] parts = traceparent.split("-");
            if (parts.length >= 4) {
                return parts[1];
            }
        } catch (Exception ignored) {
        }

        return generateTraceId();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}