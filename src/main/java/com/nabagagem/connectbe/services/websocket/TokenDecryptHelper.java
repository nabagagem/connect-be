package com.nabagagem.connectbe.services.websocket;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class TokenDecryptHelper {
    private final JwtDecoder jwtDecoder;

    public Optional<String> getUserIdFrom(String token) {
        return Optional.ofNullable(jwtDecoder.decode(token))
                .map(JwtClaimAccessor::getSubject);
    }
}
