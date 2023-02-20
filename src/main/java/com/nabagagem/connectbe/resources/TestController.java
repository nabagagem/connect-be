package com.nabagagem.connectbe.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    Test test() {
        log.info("ESTOY AQUI");
        return new Test("bar", UUID.randomUUID().toString());
    }

    @PostMapping("/test")
    Test test(@RequestBody TestInput testInput) {
        return new Test(testInput.foo(), UUID.randomUUID().toString());
    }

    private record Test(String foo, String id) {
    }

    private record TestInput(String foo) {
    }
}
