package com.nabagagem.connectbe.config.security;

import com.nabagagem.connectbe.config.TokenForwardingInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CognitoClientConfig {

    @Bean
    public RestTemplate cognitoRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                            TokenForwardingInterceptor tokenForwardingInterceptor) {
        return restTemplateBuilder.uriTemplateHandler(new RootUriTemplateHandler("https://ramifica.auth.us-east-1.amazoncognito.com"))
                .interceptors(tokenForwardingInterceptor)
                .build();
    }

}
