package com.nabagagem.connectbe.resources;

import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestController {

    @GetMapping("/test")
    Test test() {
        return new Test();
    }

    @Value
    private static class Test {
        String foo = "bar";
        String id = UUID.randomUUID().toString();
    }
}
