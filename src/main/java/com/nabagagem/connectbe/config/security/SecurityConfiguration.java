package com.nabagagem.connectbe.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtIssuerAuthenticationManagerResolver authenticationManagerResolver) throws Exception {
        http.cors()
                .configurationSource(this::getCorsSetup)
                .and()
                .csrf().disable()
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/test",
                                "/test-token",
                                "/swagger-redirect/**",
                                "/swagger-ui/**",
                                "/api/v1/ui/options",
                                "/v3/**", "/ws/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.OPTIONS)
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/profile/*",
                                "/api/v1/profile/*/pic",
                                "/api/v1/events/*/pic",
                                "/api/v1/messages/*/file",
                                "/api/v1/profile-reports/*/pics/*")

                        .permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.authenticationManagerResolver(authenticationManagerResolver);
                })
        ;
        return http.build();
    }

    @Bean
    public JwtIssuerAuthenticationManagerResolver authenticationManagerResolver(
            AuthenticationProperties authenticationProperties
    ) {
        return new JwtIssuerAuthenticationManagerResolver(
                authenticationProperties.getIssuerUrls());
    }

    @Bean
    public RestTemplate swaggerRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                            AuthenticationProperties authenticationProperties) {
        return restTemplateBuilder.rootUri(authenticationProperties.getSwaggerAuthUi())
                .build();
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
