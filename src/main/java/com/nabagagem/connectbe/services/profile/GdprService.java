package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Gdpr;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class GdprService {
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;

    public void update(UUID profileId, Gdpr gdpr) {
        ConnectProfile profile = profileService.findOrCreate(profileId);
        profile.setGdpr(gdpr);
        profileService.save(profile);
    }

    public Optional<Gdpr> get(UUID profileId) {
        return profileRepo.findById(profileId)
                .map(ConnectProfile::getGdpr);
    }
}
