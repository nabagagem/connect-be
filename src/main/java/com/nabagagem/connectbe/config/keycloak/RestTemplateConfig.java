package com.nabagagem.connectbe.config.keycloak;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate keycloakRestTemplate(RestTemplateBuilder builder,
                                             KeycloakProperties keycloakProperties) {
        return builder.rootUri(keycloakProperties.getUrl()).build();
    }

}
