package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.services.UnwrapLoggedUserIdTrait;
import com.nabagagem.connectbe.services.profile.LastActivityService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
@WebFilter(urlPatterns = "/api/**")
public class LastActivityFilter implements Filter, UnwrapLoggedUserIdTrait {
    private final LastActivityService lastActivityService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            log.info("Request path: {}", httpServletRequest.getRequestURI());
        }
        unwrapLoggedUserId()
                .ifPresent(lastActivityService::register);

        chain.doFilter(request, response);
    }
}
