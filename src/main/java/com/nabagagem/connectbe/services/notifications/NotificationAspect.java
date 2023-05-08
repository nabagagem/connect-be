package com.nabagagem.connectbe.services.notifications;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@AllArgsConstructor
public class NotificationAspect {
    private final ApplicationEventPublisher eventPublisher;

    @AfterReturning(value = "@annotation(PublishResult)", returning = "result")
    public void publish(Object result) {
        log.info("Handling annotated event: {}", result);
        eventPublisher.publishEvent(result);
    }

}
