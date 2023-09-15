package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.MediaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProfilePicService {
    private final ProfileRepo profileRepo;
    private final ProfileService profileService;
    private final MediaService mediaService;

    public void save(ProfilePicCommand profilePicCommand) {
        UUID profileId = profilePicCommand.id();
        ConnectProfile profile = profileService.findOrCreate(profileId);
        Optional.ofNullable(profile.getProfilePicture())
                .ifPresent(mediaService::delete);
        profile.setProfilePicture(mediaService.upload(profilePicCommand.file()));
        profileService.save(profile);
    }

    public Optional<Media> getPicFor(UUID id) {
        return profileRepo.findById(id)
                .map(ConnectProfile::getProfilePicture);
    }
}
