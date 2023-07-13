package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.services.LoggedUserIdTrait;
import com.nabagagem.connectbe.services.profile.LastActivityService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
@WebFilter(urlPatterns = "/api/**")
public class LastActivityFilter implements Filter, LoggedUserIdTrait {
    private final LastActivityService lastActivityService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            String requestURI = httpServletRequest.getRequestURI();
            if (requestURI.startsWith("/api")) {
                String method = httpServletRequest.getMethod();
                log.info("API Request: {} {}", method, requestURI);
                loggedUser()
                        .ifPresent(loggedUserId -> threadPoolTaskExecutor
                                .submit(() -> lastActivityService.register(loggedUserId)));
            }
        }
        chain.doFilter(request, response);
    }
}
