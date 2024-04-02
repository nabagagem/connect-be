package com.nabagagem.connectbe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class SwaggerConfig {//7lknpajgch57u8sat9opumi2eo
    private static final String OAUTH_SCHEME_NAME = "oAuth";
    private static final String PROTOCOL_URL_FORMAT = "/swagger-redirect";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows(); //3a

        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createAuthorizationCodeFlow();

        return new OAuthFlows()
                .authorizationCode(flow);
    }

    private OAuthFlow createAuthorizationCodeFlow() {
        var protocolUrl = String.format(PROTOCOL_URL_FORMAT);

        return new OAuthFlow()
                .authorizationUrl(protocolUrl + "/oauth2/authorize")
                .tokenUrl(protocolUrl + "/oauth2/token")
                .scopes(new Scopes().addString("openid", "email"));
    }
}
