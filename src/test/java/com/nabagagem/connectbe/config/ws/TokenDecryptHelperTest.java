package com.nabagagem.connectbe.config.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenDecryptHelperTest {

    @Mock
    private JwtDecoder mockJwtDecoder;

    private TokenDecryptHelper tokenDecryptHelperUnderTest;

    @BeforeEach
    void setUp() {
        tokenDecryptHelperUnderTest = new TokenDecryptHelper(mockJwtDecoder);
    }

    @Test
    void testGetSubFrom() {
        // Setup
        // Configure JwtDecoder.decode(...).
        final Jwt jwt = new Jwt("tokenValue", LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                LocalDateTime.of(2020, 1, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC),
                Map.ofEntries(Map.entry("value", "value")), Map.ofEntries(Map.entry("sub", "123")));
        when(mockJwtDecoder.decode("token")).thenReturn(jwt);

        // Run the test
        final Optional<String> result = tokenDecryptHelperUnderTest.getSubFrom("token");

        // Verify the results
        assertThat(result).isEqualTo(Optional.of("123"));
    }

    @Test
    void testGetSubFrom_JwtDecoderReturnsNull() {
        // Setup
        when(mockJwtDecoder.decode("token")).thenReturn(null);

        // Run the test
        final Optional<String> result = tokenDecryptHelperUnderTest.getSubFrom("token");

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testGetSubFrom_JwtDecoderThrowsJwtException() {
        // Setup
        when(mockJwtDecoder.decode("token")).thenThrow(JwtException.class);

        // Run the test
        assertThatThrownBy(() -> tokenDecryptHelperUnderTest.getSubFrom("token")).isInstanceOf(JwtException.class);
    }
}
