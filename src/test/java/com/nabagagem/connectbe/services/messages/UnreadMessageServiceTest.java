package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.profile.ProfileMailPersonalInfo;
import com.nabagagem.connectbe.entities.UserMailNotification;
import com.nabagagem.connectbe.repos.UserMailNotificationRepository;
import com.nabagagem.connectbe.services.notifications.MailGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnreadMessageServiceTest {

    @Mock
    private MailGateway mockMailGateway;
    @Mock
    private UserMailNotificationRepository mockUserMailNotificationRepository;

    private UnreadMessageService unreadMessageServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        unreadMessageServiceUnderTest = new UnreadMessageService(mockMailGateway, mockUserMailNotificationRepository);
    }

    @Test
    void testSendUnreadEmail() {
        // Setup
        UUID profileId = UUID.randomUUID();
        final ProfileMailPersonalInfo profile = new ProfileMailPersonalInfo() {
            @Override
            public UUID getId() {
                return profileId;
            }

            @Override
            public PersonalInfoInfo getPersonalInfo() {
                return new PersonalInfoInfo() {
                    @Override
                    public String getPublicName() {
                        return "public name";
                    }

                    @Override
                    public String getSlug() {
                        return null;
                    }

                    @Override
                    public String getEmail() {
                        return "mail@mail.com";
                    }

                    @Override
                    public Boolean isEnableMessageEmail() {
                        return true;
                    }
                };
            }
        };

        // Run the test
        unreadMessageServiceUnderTest.sendUnreadEmail(profile);

        // Verify the results
        verify(mockMailGateway).sendUnreadEmailNotification(any(ProfileMailPersonalInfo.class));
        verify(mockUserMailNotificationRepository).deleteByProfileId(
                profileId);
        verify(mockUserMailNotificationRepository).save(any(UserMailNotification.class));
    }

}
