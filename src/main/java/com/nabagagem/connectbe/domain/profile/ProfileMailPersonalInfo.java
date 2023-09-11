package com.nabagagem.connectbe.domain.profile;

import java.util.UUID;

/**
 * Projection for {@link com.nabagagem.connectbe.entities.ConnectProfile}
 */
public interface ProfileMailPersonalInfo {
    UUID getId();

    PersonalInfoInfo getPersonalInfo();

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.PersonalInfo}
     */
    interface PersonalInfoInfo {
        String getPublicName();

        String getSlug();

        String getEmail();

        Boolean isEnableMessageEmail();
    }
}