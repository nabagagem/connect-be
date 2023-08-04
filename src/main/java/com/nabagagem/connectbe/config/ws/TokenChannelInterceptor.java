package com.nabagagem.connectbe.config.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Optional;

@SuppressWarnings({"unchecked", "DataFlowIssue"})
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.secured")
public class TokenChannelInterceptor implements ChannelInterceptor {
    private final TokenRepo tokenRepo;
    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return handleWsMessage(message);
    }

    private Message<?> handleWsMessage(Message<?> message) {
        MessageHeaders headers = message.getHeaders();
        String simpMessageType = headers.getOrDefault("simpMessageType", "").toString();
        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        String simpSessionId = headers.get("simpSessionId").toString();
        log.info("handling pre send for session: {} with action: {} and headers: {}", simpSessionId, simpMessageType,
                multiValueMap);
        if (simpMessageType.equals("CONNECT")) {
            handleConnect(simpSessionId, multiValueMap);
        }
        if (simpMessageType.equals("SUBSCRIBE")) {
            handleSubscribe(multiValueMap, simpSessionId);
        }
        if (simpMessageType.equals("DISCONNECT")) {
            tokenRepo.remove(simpSessionId);
        }
        return message;
    }

    private void handleConnect(String simpSessionId, MultiValueMap<String, String> multiValueMap) {
        Optional.ofNullable(multiValueMap)
                .map(__ -> multiValueMap.getFirst("Token"))
                .filter(StringUtils::isNotBlank)
                .ifPresentOrElse(token -> {
                    log.info("Assigning token for session: {}: {}...", simpSessionId, token.substring(0, 5));
                    tokenRepo.put(simpSessionId, token);
                }, () -> log.warn("CONNECT attempt without token for session: {}", simpSessionId));
    }

    private void handleSubscribe(MultiValueMap<String, String> multiValueMap, String simpSessionId) {
        Optional.ofNullable(multiValueMap.getFirst("destination"))
                .map(s -> s.split("/"))
                .filter(parts -> parts.length > 1)
                .ifPresentOrElse(destinationParts -> {
                    String topicUserId = destinationParts[destinationParts.length - 1];
                    log.info("Topic user id: {}", topicUserId);
                    Optional.ofNullable(tokenRepo.get(simpSessionId))
                            .ifPresentOrElse(token -> {
                                log.info("Token for user session {}: {}...", simpSessionId, token.substring(0, 5));
                                Jwt jwt = jwtDecoder.decode(token);
                                String sub = jwt.getClaim("sub").toString();
                                if (!sub.equals(topicUserId)) {
                                    log.warn("Session token sub does not match the topic user id: {} - {}", sub, topicUserId);
                                    throw new AccessDeniedException("Unauthorized");
                                }
                                log.info("User {} authenticated on WS session {}", topicUserId, simpSessionId);
                            }, () -> {
                                log.warn("No token set on session: {}", simpSessionId);
                                throw new AccessDeniedException("Unauthorized");
                            });

                }, () -> log.info("No destination set on request"));
    }
}
