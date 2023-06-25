package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.EventNotification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Slf4j
@Component
@AllArgsConstructor
public class NotificationAspect {
    private final ApplicationEventPublisher eventPublisher;

    @Around("@annotation(PublishNotification)")
    public Object publish(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Handling annotated event");
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Object result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        Optional.ofNullable(methodSignature.getMethod().getAnnotation(PublishNotification.class))
                .ifPresent(publishNotification -> {
                    eventPublisher.publishEvent(new EventNotification(publishNotification, result));
                });
        return result;
    }
}
