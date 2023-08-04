package com.nabagagem.connectbe.config.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenChannelInterceptorTest {

    @Mock
    private TokenRepo mockTokenRepo;
    @Mock
    private TokenDecryptHelper mockTokenDecryptHelper;

    private TokenChannelInterceptor tokenChannelInterceptorUnderTest;

    @BeforeEach
    void setUp() {
        tokenChannelInterceptorUnderTest = new TokenChannelInterceptor(mockTokenRepo, mockTokenDecryptHelper);
    }

    @Test
    void testPreSend_ConnectWithToken() {
        // Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("CONNECT");
        when(headers.get("simpSessionId")).thenReturn("123");

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        headersMap.put("Token", Collections.singletonList("666"));
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);

        // Run the test
        tokenChannelInterceptorUnderTest.preSend(message, null);

        // Verify the results
        verify(mockTokenRepo).put("123", "666");
    }

    @Test
    void testPreSend_ConnectWithoutToken() {
        // Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("CONNECT");
        when(headers.get("simpSessionId")).thenReturn("123");

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);

        // Run the test
        assertThatThrownBy(() -> tokenChannelInterceptorUnderTest.preSend(message, null))
                .isInstanceOf(AccessDeniedException.class);

        // Verify the results
        verify(mockTokenRepo, never()).put(anyString(), anyString());
    }

    @Test
    void testPreSend_SubscribeWithTokenMatching() {
        // Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("SUBSCRIBE");
        String sessionId = "123";
        when(headers.get("simpSessionId")).thenReturn(sessionId);
        String topicUserId = "999";

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);
        headersMap.put("destination", Collections.singletonList(String.format("/topics/user/%s", topicUserId)));

        String token = "the-token";
        when(mockTokenRepo.get(sessionId)).thenReturn(token);
        when(mockTokenDecryptHelper.getSubFrom(token)).thenReturn(Optional.of(topicUserId));

        //Run
        tokenChannelInterceptorUnderTest.preSend(message, null);
    }

    @Test
    void testPreSend_SubscribeWithoutToken() {
        // Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("SUBSCRIBE");
        String sessionId = "123";
        when(headers.get("simpSessionId")).thenReturn(sessionId);
        String topicUserId = "999";

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);
        headersMap.put("destination", Collections.singletonList(String.format("/topics/user/%s", topicUserId)));

        when(mockTokenRepo.get(sessionId)).thenReturn(null);

        //Run
        assertThatThrownBy(() -> tokenChannelInterceptorUnderTest.preSend(message, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testPreSend_SubscribeWithTokenNotMatching() {
        // Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("SUBSCRIBE");
        String sessionId = "123";
        when(headers.get("simpSessionId")).thenReturn(sessionId);
        String topicUserId = "999";

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);
        headersMap.put("destination", Collections.singletonList(String.format("/topics/user/%s", topicUserId)));

        String token = "the-token";
        when(mockTokenRepo.get(sessionId)).thenReturn(token);
        when(mockTokenDecryptHelper.getSubFrom(token)).thenReturn(Optional.of("foobar"));

        //Run
        assertThatThrownBy(() -> tokenChannelInterceptorUnderTest.preSend(message, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testPreSend_Disconnect() {
        //Setup
        final Message<?> message = mock(Message.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(message.getHeaders()).thenReturn(headers);
        when(headers.getOrDefault("simpMessageType", "")).thenReturn("DISCONNECT");
        String sessionId = "123";
        when(headers.get("simpSessionId")).thenReturn(sessionId);
        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        when(headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class))
                .thenReturn(headersMap);

        //Run
        tokenChannelInterceptorUnderTest.preSend(message, null);
        verify(mockTokenRepo).remove(sessionId);
    }
}
