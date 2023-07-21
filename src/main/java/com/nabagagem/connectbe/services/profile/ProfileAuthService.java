package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.LoggedUserIdTrait;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileAuthService implements LoggedUserIdTrait {
    private final ProfileRepo profileRepo;

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
        return loggedUser()
                .filter(loggedUserId -> profile.id().equals(loggedUserId)
                        || isAltFrom(profile, loggedUserId))
                .isPresent();
    }

    private boolean isAltFrom(ProfilePayload profile, UUID loggedUserId) {
        return Optional.ofNullable(profile.parentId())
                .filter(loggedUserId::equals)
                .isPresent();
    }

    private Boolean isPublic(ProfilePayload profile) {
        return Optional.ofNullable(profile.personalInfo())
                .map(PersonalInfo::getPublicProfile)
                .orElse(false);
    }

    public void failIfNotLoggedIn(UUID profileId) {
        loggedUser()
                .filter(loggedUser -> profileId.equals(loggedUser)
                        || profileRepo.isAltFrom(profileId, loggedUser))
                .ifPresentOrElse(uuid -> {
                }, () -> {
                    throw new AccessDeniedException("Unauthorized");
                });
    }

    public void failIfNotCurrentProfile(UUID profileId) {
        loggedUser()
                .filter(profileId::equals)
                .ifPresentOrElse(uuid -> {
                }, () -> {
                    throw new AccessDeniedException("Unauthorized");
                });
    }
}
