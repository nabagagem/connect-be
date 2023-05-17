package com.nabagagem.connectbe.repos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@Validated
public class TestController {

    @GetMapping("/test")
    Test test() {
        return new Test("bar", UUID.randomUUID().toString());
    }

    @PostMapping("/test")
    Test test(@RequestBody @Valid TestInput testInput) {
        return new Test(testInput.foo(), UUID.randomUUID().toString());
    }

    private record Test(String foo, String id) {
    }

    private record TestInput(@NotEmpty String foo) {
    }
}
