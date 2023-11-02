package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GetProfileService {
    private final ProfileService profileService;
    private final ProfilePayloadMapper profilePayloadMapper;

    public ProfilePayload getProfile(UUID id) {
        ConnectProfile profile = profileService.findOrCreate(id);
        return profilePayloadMapper.toPayload(profile);
    }
}
