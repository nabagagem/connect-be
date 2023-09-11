package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.profile.ProfileMailPersonalInfo;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.UserMailNotification;
import com.nabagagem.connectbe.repos.UserMailNotificationRepository;
import com.nabagagem.connectbe.services.notifications.MailGateway;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@Transactional
@AllArgsConstructor
public class UnreadMessageService {
    private final MailGateway mailGateway;
    private final UserMailNotificationRepository userMailNotificationRepository;

    public void sendUnreadEmail(ProfileMailPersonalInfo profile) {
        mailGateway.sendUnreadEmailNotification(profile);
        userMailNotificationRepository.deleteByProfileId(profile.getId());
        userMailNotificationRepository.save(
                UserMailNotification.builder()
                        .sentAt(ZonedDateTime.now())
                        .profile(ConnectProfile.builder().id(profile.getId()).build())
                        .build());
    }
}
