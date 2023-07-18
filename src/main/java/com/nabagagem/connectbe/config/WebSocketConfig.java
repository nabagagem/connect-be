package com.nabagagem.connectbe.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtDecoder jwtDecoder;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/all", "/topics");
        config.setApplicationDestinationPrefixes("/socket");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            private final Map<String, String> sessionTokens = new HashMap<>();

            @SuppressWarnings({"unchecked", "DataFlowIssue"})
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                MessageHeaders headers = message.getHeaders();
                String simpMessageType = headers.getOrDefault("simpMessageType", "").toString();
                MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
                String simpSessionId = headers.get("simpSessionId").toString();
                if (simpMessageType.equals("CONNECT")) {
                    sessionTokens.put(simpSessionId, multiValueMap.getFirst("Token"));
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
        });
    }
}
