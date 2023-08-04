package com.nabagagem.connectbe.services.websocket;

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
    private final TokenDecryptHelper tokenDecryptHelper;

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
        switch (simpMessageType) {
            case "CONNECT" -> handleConnect(simpSessionId, multiValueMap);
            case "SUBSCRIBE" -> handleSubscribe(multiValueMap, simpSessionId);
            case "DISCONNECT" -> tokenRepo.remove(simpSessionId);
        }
        return message;
    }

    private void handleConnect(String simpSessionId, MultiValueMap<String, String> multiValueMap) {
        Optional.ofNullable(multiValueMap)
                .map(__ -> multiValueMap.getFirst("Token"))
                .filter(StringUtils::isNotBlank)
                .ifPresentOrElse(
                        token -> tokenRepo.put(simpSessionId, token),
                        () -> {
                            throw new AccessDeniedException("Unauthorized");
                        });
    }

    private void handleSubscribe(MultiValueMap<String, String> multiValueMap, String simpSessionId) {
        Optional.ofNullable(multiValueMap.getFirst("destination"))
                .map(s -> s.split("/"))
                .filter(parts -> parts.length > 1)
                .ifPresentOrElse(destinationParts -> {
                    String topicUserId = destinationParts[destinationParts.length - 1];
                    log.info("Topic user id: {}", topicUserId);
                    Optional.ofNullable(tokenRepo.get(simpSessionId))
                            .flatMap(tokenDecryptHelper::getUserIdFrom)
                            .filter(topicUserId::equals)
                            .ifPresentOrElse(
                                    __ -> log.info("User {} authenticated on WS session {}", topicUserId, simpSessionId),
                                    () -> {
                                        log.warn("Token does not match user session: {}", simpSessionId);
                                        throw new AccessDeniedException("Unauthorized");
                                    });
                }, () -> log.info("No destination set on request"));
    }
}
