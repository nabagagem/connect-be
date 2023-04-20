package com.nabagagem.connectbe.clients;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class KeycloakFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Authorization", "Bearer " +
                getToken());
    }

    private String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
        }
        return null;
    }
}
