package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.services.profile.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class AuthService {
    private final SlugService slugService;

    public ConnectProfile initFromAuth(UUID id) {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken) {
                        Map<String, Object> claims = ((JwtAuthenticationToken) authentication).getToken().getClaims();
                        String publicName = getNameFrom(claims);
                        return ConnectProfile.builder()
                                .id(id)
                                .personalInfo(
                                        PersonalInfo.builder()
                                                .publicName(publicName)
                                                .slug(
                                                        Optional.ofNullable(publicName)
                                                                .map(this::generateSlug)
                                                                .orElse(null)
                                                )
                                                .build())
                                .build();
                    }
                    return null;
                }).orElseGet(() -> ConnectProfile.builder().build());
    }

    private String generateSlug(String publicName) {
        String slug = publicName.replaceAll("\\s", "-")
                .toLowerCase().trim();
        return IntStream.range(0, 3)
                .mapToObj(t -> slug.concat(t == 0 ? "" : String.valueOf(t)))
                .filter(slugService::doesNotExists)
                .findAny().orElse(slug);
    }

    private String getNameFrom(Map<String, Object> claims) {
        return claims.getOrDefault("name", "").toString();
    }
}
