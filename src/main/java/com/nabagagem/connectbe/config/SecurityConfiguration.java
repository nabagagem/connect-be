package com.nabagagem.connectbe.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
                .configurationSource(this::getCorsSetup)
                .and()
                .csrf().disable()
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/test",
                                "/swagger-ui/**",
                                "/api/v1/ui/options",
                                "/v3/**", "/ws/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.OPTIONS)
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/profile/*/pic", "/api/v1/events/*/pic", "/api/v1/profile-reports/*/pics/*")
                        .permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer()
                .jwt()
        ;
        return http.build();
    }

    private CorsConfiguration getCorsSetup(HttpServletRequest httpServletRequest) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        return configuration;
    }
}
