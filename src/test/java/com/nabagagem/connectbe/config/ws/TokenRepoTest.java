package com.nabagagem.connectbe.config.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRepoTest {

    private TokenRepo tokenRepoUnderTest;

    @BeforeEach
    void setUp() {
        tokenRepoUnderTest = new TokenRepo();
    }

    @Test
    void testRemoveEldestEntry() {
        IntStream.range(0, 102)
                .mapToObj(String::valueOf)
                .forEach(value -> tokenRepoUnderTest.put(value, value));
        assertThat(tokenRepoUnderTest).hasSize(100);
        assertThat(tokenRepoUnderTest).doesNotContainKeys("0", "1");
        assertThat(tokenRepoUnderTest).containsKeys("2", "101");
    }
}
