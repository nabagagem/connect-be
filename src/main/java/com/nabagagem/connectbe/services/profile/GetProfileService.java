package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GetProfileService {
    private final ProfileRepo profileRepo;
    private final ProfileService profileService;
    private final ProfilePayloadMapper profilePayloadMapper;

    public ProfilePayload getProfile(UUID id, UUID loggedUserId) {
        ConnectProfile profile = profileRepo.findForProfileRead(id)
                .orElseGet(() -> Optional.ofNullable(loggedUserId)
                        .filter(id::equals)
                        .map(profileService::init)
                        .map(ConnectProfile::getId)
                        .flatMap(profileRepo::findForProfileRead)
                        .orElseThrow(ProfileNotFoundException::new));
        return profilePayloadMapper.toPayload(profile);
    }
}
