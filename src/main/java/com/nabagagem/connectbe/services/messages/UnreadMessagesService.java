package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.services.notifications.MailGateway;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UnreadMessagesService {
    private final MessageRepo messageRepo;
    private final MailGateway mailGateway;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void run() {
        messageRepo.findProfilesWithUnreadMessages(ZonedDateTime.now().minusSeconds(10))
                .forEach(this::sendUnreadEmail);
    }

    private void sendUnreadEmail(ConnectProfile profile) {
        mailGateway.sendUnreadEmailNotification(profile);
    }

}
