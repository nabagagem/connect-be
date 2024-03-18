package com.nabagagem.connectbe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class SwaggerConfig {
    private static final String OAUTH_SCHEME_NAME = "oAuth";
    private static final String PROTOCOL_URL_FORMAT = "https://cognito.ramifica.eu";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components() //1
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME));
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows(); //3a

        return new SecurityScheme() //2
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

        //client_id: 4j5c9fc4t6ajivog981lpe5kod
        //password: ^i4ZDyA1k4tikes

        return new OAuthFlow()//^i4ZDyA1k4tikes
                .authorizationUrl(protocolUrl + "/oauth2/authorize")
                .tokenUrl(protocolUrl + "/oauth2/token")
                .scopes(new Scopes().addString("openid", "email"));
    }
}
