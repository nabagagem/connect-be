package com.nabagagem.connectbe.controllers.profile;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TestTokenController {

    @GetMapping("/api/v1/token")
    public Map<String, String> get(Principal principal) {
        return Map.of("foo", principal.getName());
    }

}
