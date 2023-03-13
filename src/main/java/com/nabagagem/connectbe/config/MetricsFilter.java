package com.nabagagem.connectbe.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@WebFilter(filterName = "metricsFilter", urlPatterns = "/**")
public class MetricsFilter extends OncePerRequestFilter {
    private final ObservationRegistry observationRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String metricName = request.getMethod().concat(request.getRequestURI())
                .replaceAll("/", ".");
        log.info("Request: {}", metricName);
        Observation.createNotStarted(metricName, observationRegistry)
                .observe(() -> {
                    try {
                        filterChain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
