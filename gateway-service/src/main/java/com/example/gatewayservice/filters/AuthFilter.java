package com.example.gatewayservice.filters;

import com.example.gatewayservice.dto.AuthResponseDTO;
import com.example.gatewayservice.exeptions.InvalidApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.UUID;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private final WebClient webClient = WebClient.create("http://auth-service:8085");

    private static final List<String> EXCLUDED_PATHS = List.of(
            "v3/api-docs",
            "swagger-ui",
            "swagger-config",
            "webjars"
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

        String traceId = UUID.randomUUID().toString();
        log.info("[traceId={}] GATEWAY: [{}] {}", traceId, method, path);

        if (EXCLUDED_PATHS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey == null) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "X-API-KEY fali!",traceId);
        }



        return webClient.get()
                .uri("/api/auth/validate")
                .header("X-API-KEY", apiKey)
                .header("X-Trace-Id", traceId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Nevalidan kljuc")
                                .flatMap(body -> Mono.error(new InvalidApiKeyException("Nevalidan kljuc")))
                )
                .bodyToMono(AuthResponseDTO.class)
                .flatMap(auth -> {
                    if (!auth.isValid()) {
                        return onError(exchange, HttpStatus.FORBIDDEN, "Nevalidan kljuc",traceId);
                    }

                    List<String> roles = auth.getRoles();

                    boolean isProtected = PROTECTED_KEYWORDS.stream().anyMatch(path::contains);

                    if (isProtected) {
                        if (method.equals("DELETE") && !roles.contains("ADMIN")) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Samo admin brise",traceId);
                        }

                        if ((method.equals("POST") || method.equals("PUT")) &&
                                (!roles.contains("ADMIN") && !roles.contains("GUIDE"))) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Guest ne moze da kerira i menja",traceId);
                        }
                    }

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Role", String.join(",", roles))
                            .header("X-Authenticated-User", auth.getUsername())
                            .header("X-Trace-Id", traceId)
                            .build();

                    //return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    return chain.filter(exchange.mutate().request(modifiedRequest).build())
                            .doOnSubscribe(sub -> {
                                exchange.getResponse().beforeCommit(() -> {
                                    exchange.getResponse().getHeaders()
                                            .add("X-Processing-Time",
                                                    (System.currentTimeMillis() - startTime) + "ms");
                                    exchange.getResponse().getHeaders()
                                            .add("X-Trace-Id", traceId);
                                    exchange.getResponse().getHeaders()
                                            .add("X-Eco-Service-Status", "ECO-TOURISM-OK");
                                    exchange.getResponse().getHeaders()
                                            .add("X-Served-By", "Gateway-v1.0");
                                    return Mono.empty();
                                });
                            });
                })
                .onErrorResume(e -> {
                    if (e instanceof InvalidApiKeyException) {
                        return onError(exchange, HttpStatus.FORBIDDEN, e.getMessage(),traceId);
                    }
                    return onError(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Auth servis nedostupan",traceId);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message, String traceId) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String timestamp = java.time.LocalDateTime.now().toString();
        String body = String.format(
                "{\"timestamp\": \"%s\", \"message\": \"%s\", \"traceId\": \"%s\", \"status\": %d}",
                timestamp, message, traceId, status.value()
        );
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() { return -1; }
}
