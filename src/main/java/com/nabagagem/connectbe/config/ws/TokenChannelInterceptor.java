package com.nabagagem.connectbe.config.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.secured")
public class TokenChannelInterceptor implements ChannelInterceptor {
    private final Map<String, String> sessionTokens = new HashMap<>();
    private final JwtDecoder jwtDecoder;

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        String simpMessageType = headers.getOrDefault("simpMessageType", "").toString();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        String simpSessionId = headers.get("simpSessionId").toString();
        if (simpMessageType.equals("CONNECT")) {
            String token = multiValueMap.getFirst("Token");
            log.info("Assigning token for session: {}: {}...", simpSessionId, token.substring(0, 5));
            sessionTokens.put(simpSessionId, token);
        }
        if (simpMessageType.equals("SUBSCRIBE")) {
            String destination = multiValueMap.getFirst("destination");
            String[] destinationParts = destination
                    .split("/");
            String topicUserId = destinationParts[destinationParts.length - 1];
            log.info("Topic user id: {}", topicUserId);
            Optional.ofNullable(sessionTokens.get(simpSessionId))
                    .ifPresentOrElse(token -> {
                        log.info("Token for user session: {}", token);
                        Jwt jwt = jwtDecoder.decode(token);
                        String sub = jwt.getClaim("sub").toString();
                        if (!sub.equals(topicUserId)) {
                            log.warn("Session token sub does not match the topic user id: {} - {}", sub, topicUserId);
                            throw new AccessDeniedException("Unauthorized");
                        }
                    }, () -> {
                        log.warn("No token set on session: {}", simpSessionId);
                        throw new AccessDeniedException("Unauthorized");
                    });
        }
        return message;
    }
}
