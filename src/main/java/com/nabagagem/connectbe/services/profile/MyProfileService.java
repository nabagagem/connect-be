package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.mappers.ProfilePayloadMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MyProfileService {
    private final ProfileRepo profileRepo;
    private final ProfilePayloadMapper profilePayloadMapper;

    public ProfilePayload getMyProfileFor(UUID profileId) {
        return profilePayloadMapper.toPayload(profileRepo.getMyProfile(profileId), true);
    }
}
