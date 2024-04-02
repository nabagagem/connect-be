package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.services.profile.LastActivityService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/api/**")
public class LastActivityFilter implements Filter {
    private final LastActivityService lastActivityService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final LoginHelper loginHelper;

    public LastActivityFilter(@Autowired(required = false) LastActivityService lastActivityService,
                              @Autowired(required = false) ThreadPoolTaskExecutor threadPoolTaskExecutor,
                              LoginHelper loginHelper) {
        this.lastActivityService = lastActivityService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.loginHelper = loginHelper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (lastActivityService != null
                && threadPoolTaskExecutor != null
                && request instanceof HttpServletRequest httpServletRequest) {
            String requestURI = httpServletRequest.getRequestURI();
            if (requestURI.startsWith("/api")) {
                loginHelper.loggedUser()
                        .ifPresent(loggedUserId -> threadPoolTaskExecutor
                                .submit(() -> lastActivityService.register(loggedUserId)));
            }
        }

        chain.doFilter(request, response);
    }
}
