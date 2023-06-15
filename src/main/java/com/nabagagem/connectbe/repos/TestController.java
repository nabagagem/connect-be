package com.nabagagem.connectbe.repos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
public class TestController {

    @GetMapping("/test")
    Test test(@Header("Accept-Language") String language) {
        log.info("accept: {}", language);
        log.info("locale: {}", LocaleContextHolder.getLocale());
        return new Test("bar", "id");
    }

    @PostMapping("/test")
    Test test(@RequestBody @Valid TestInput testInput) {
        return new Test(testInput.foo(), "id");
    }

    private record Test(String foo, String id) {
    }

    private record TestInput(@NotEmpty String foo) {
    }
}
