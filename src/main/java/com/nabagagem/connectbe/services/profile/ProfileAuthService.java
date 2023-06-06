package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.ProfilePayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.services.UnwrapLoggedUserIdTrait;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileAuthService implements UnwrapLoggedUserIdTrait {
    public ProfilePayload isAllowedOn(ProfilePayload profile) {
        if (profile.id() == null || isPublic(profile) || isOwnerOf(profile)) {
            return profile;
        }

        throw new AccessDeniedException("Unauthorized");
    }

    private boolean isOwnerOf(ProfilePayload profile) {
        if (profile.id() == null) {
            return false;
        }
        return unwrapLoggedUserId()
                .filter(uuid -> profile.id().equals(uuid))
                .isPresent();
    }

    private Boolean isPublic(ProfilePayload profile) {
        return Optional.ofNullable(profile.personalInfo())
                .map(PersonalInfo::getPublicProfile)
                .orElse(false);
    }

    public void failIfNotLoggedIn(UUID profileId) {
        unwrapLoggedUserId()
                .filter(profileId::equals)
                .ifPresentOrElse(uuid -> {
                }, () -> {
                    throw new AccessDeniedException("Unauthorized");
                });
    }
}
