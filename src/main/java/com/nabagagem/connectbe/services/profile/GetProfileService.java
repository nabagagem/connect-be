package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GetProfileService {
    private final ProfileService profileService;
    private final ProfilePayloadMapper profilePayloadMapper;
    private final ProfileRepo profileRepo;

    public ProfilePayload getProfile(UUID id) {
        profileService.findOrCreate(id);
        return profilePayloadMapper.toPayload(profileRepo.findForProfileRead(id).orElseThrow());
    }
}
