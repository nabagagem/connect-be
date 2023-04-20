package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    public ConnectProfile initFromAuth(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Map<String, Object> claims = ((JwtAuthenticationToken) authentication).getToken().getClaims();
            return ConnectProfile.builder()
                    .id(id)
                    .personalInfo(
                            PersonalInfo.builder()
                                    .publicName(getNameFrom(claims))
                                    .build())
                    .build();
        }
        throw new UsernameNotFoundException(id.toString());
    }

    private String getNameFrom(Map<String, Object> claims) {
        return claims.getOrDefault("name", "").toString();
    }
}