package org.raflab.guideservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.raflab.guideservice.errorHandling.BadRequestException;
import org.raflab.guideservice.errorHandling.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignRequestInterceptorConfig {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_TRACE_ID = "X-Trace-Id";
    private static final String X_AUTHENTICATED_USER = "X-Authenticated-User";
    private static final String X_USER_ROLE = "X-User-Role";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes == null) return;

                HttpServletRequest request = attributes.getRequest();

                copy(request, template, X_API_KEY);
                copy(request, template, X_TRACE_ID);
                copy(request, template, X_AUTHENTICATED_USER);
                copy(request, template, X_USER_ROLE);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    private void copy(HttpServletRequest request, RequestTemplate template, String header) {
        String value = request.getHeader(header);
        if (value != null && !value.isBlank()) {
            template.header(header, value);
        }
    }
}