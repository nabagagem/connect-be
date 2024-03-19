package com.nabagagem.connectbe.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "ramifica.authentication")
public class AuthenticationProperties {
    private Set<String> issuerUrls;
    private String swaggerAuthUi;
}
