package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.services.notifications.LastActivityService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
@WebFilter(filterName = "metricsFilter", urlPatterns = "/**")
public class MetricsFilter extends OncePerRequestFilter {
    private final ObservationRegistry observationRegistry;
    private final LastActivityService lastActivityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .filter(StringUtils::isNotEmpty)
                .filter(userName -> !userName.equalsIgnoreCase("anonymousUser"))
                .ifPresent(lastActivityService::register);

        Observation.createNotStarted("http.timing", observationRegistry)
                .observe(() -> {
                    try {
                        filterChain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
