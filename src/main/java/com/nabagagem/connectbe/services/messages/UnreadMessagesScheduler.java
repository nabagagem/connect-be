package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.repos.MessageRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class UnreadMessagesScheduler {
    private final MessageRepo messageRepo;
    private final UnreadMessageService unreadMessageService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void run() {
        messageRepo.findProfilesWithUnreadMessages(ZonedDateTime.now().minusMinutes(10))
                .stream().peek(profile -> log.info("Profile have unread mails: {}", profile.getId()))
                .forEach(profileMailPersonalInfo -> threadPoolTaskExecutor
                        .submit(() -> unreadMessageService.sendUnreadEmail(profileMailPersonalInfo)));
    }
}
