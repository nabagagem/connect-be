package com.nabagagem.connectbe.config.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Controller
@Slf4j
@AllArgsConstructor
public class SwaggerRedirectController {
    private final RestTemplate swaggerRestTemplate;
    private final AuthenticationProperties authenticationProperties;

    @GetMapping("/swagger-redirect/oauth2/authorize")
    public RedirectView get(@RequestParam("client_id") String clientId,
                            @RequestParam("redirect_uri") String redirect,
                            @RequestParam String state) {
        log.info("Params: {} {} {}", clientId, redirect, state);
        String url = String.format("%s/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
                authenticationProperties.getSwaggerAuthUi(),
                clientId, URLEncoder.encode(Objects.requireNonNull(fixRedirect(redirect)), StandardCharsets.UTF_8), state);
        log.info(url);
        return new RedirectView(url);
    }

    private String fixRedirect(String redirect) {
        return redirect.contains("localhost") ? redirect : redirect.replaceFirst("http", "https");
    }


    @PostMapping("/swagger-redirect/oauth2/token")
    @GetMapping("/swagger-redirect/oauth2/token")
    public ResponseEntity<String> post(@ModelAttribute TokenRequest tokenRequest) {
        return swaggerRestTemplate.postForEntity("/oauth2/token", buildRequest(tokenRequest), String.class);
    }

    @SuppressWarnings("rawtypes")
    private HttpEntity buildRequest(TokenRequest tokenRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", tokenRequest.grant_type());
        map.add("code", tokenRequest.code());
        map.add("client_id", tokenRequest.client_id());
        map.add("redirect_uri", fixRedirect(tokenRequest.redirect_uri()));

        return new HttpEntity<>(map, headers);
    }
}
