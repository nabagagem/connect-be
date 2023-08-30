package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.profile.AdminProfileCommand;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.ProfileService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProfileAdminService {
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;

    public void update(UUID id, AdminProfileCommand adminProfileCommand) {
        profileRepo.findById(id)
                .stream()
                .peek(connectProfile -> connectProfile.setProfileType(adminProfileCommand.profileType()))
                .forEach(profileService::save);
    }
}
