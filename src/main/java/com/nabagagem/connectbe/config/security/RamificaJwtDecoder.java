package com.nabagagem.connectbe.config.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RamificaJwtDecoder implements JwtDecoder {
    private final AuthenticationProperties authenticationProperties;
    private List<JwtDecoder> jwtDecoders;

    @PostConstruct
    private void load() {
        jwtDecoders = authenticationProperties.getIssuerUrls()
                .stream().map(this::buildDecoder)
                .collect(Collectors.toList());
    }

    private JwtDecoder buildDecoder(String issuer) {
        return JwtDecoders.fromIssuerLocation(issuer);
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        for (JwtDecoder jwtDecoder : jwtDecoders) {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                log.warn("Failed to decrypt token: {}", e.getMessage());
            }
        }
        throw new JwtException("Invalid token");
    }
}
