package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.ProfilePicCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.resources.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfilePicService {
    private final ProfileRepo profileRepo;
    private final ProfileService profileService;
    private final MediaService mediaService;

    public void save(ProfilePicCommand profilePicCommand) {
        ConnectProfile profile = profileService.findOrInit(profilePicCommand.id());
        profile.setProfilePicture(mediaService.toMedia(profilePicCommand.file(), profile));
        profileService.save(profile);
    }

    public Optional<Media> getPicFor(UUID id) {
        return profileRepo.findById(id)
                .map(ConnectProfile::getProfilePicture);
    }
}
