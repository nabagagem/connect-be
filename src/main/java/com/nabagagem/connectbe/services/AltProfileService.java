package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.AltProfileItem;
import com.nabagagem.connectbe.domain.commands.AltProfileCommand;
import com.nabagagem.connectbe.domain.exceptions.ConflictException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.ProfileService;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AltProfileService {
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;
    private final SlugService slugService;

    public ConnectProfile create(UUID mainProfileId,
                                 AltProfileCommand altProfileCommand) {
        ConnectProfile mainProfile = profileRepo.findById(mainProfileId).orElseThrow();
        Optional.ofNullable(mainProfile.getParentProfile())
                .ifPresent(__ -> {
                    throw ConflictException.builder()
                            .errorType(ErrorType.INVALID_PARENT_PROFILE)
                            .build();
                });
        Optional<PersonalInfo> personalInfo = Optional.ofNullable(mainProfile.getPersonalInfo());
        ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.randomUUID())
                .parentProfile(mainProfile)
                .personalInfo(PersonalInfo.builder()
                        .publicName(personalInfo.map(PersonalInfo::getPublicName).orElse(null))
                        .slug(
                                personalInfo
                                        .map(PersonalInfo::getPublicName)
                                        .map(slugService::generateSlug)
                                        .orElse(null)
                        )
                        .profileCategory(altProfileCommand.profileCategory())
                        .highlightTitle(altProfileCommand.highlightTitle())
                        .profession(altProfileCommand.profession())
                        .amountPerHour(Objects.requireNonNull(
                                personalInfo.map(PersonalInfo::getAmountPerHour).orElse(null)))
                        .workingMode(personalInfo.map(PersonalInfo::getWorkingMode).orElse(null))
                        .build())
                .build();
        return profileService.save(profile);
    }

    public List<AltProfileItem> listFor(UUID profileId) {
        return profileRepo.listFor(profileId);
    }
}
