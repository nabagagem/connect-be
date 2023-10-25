package com.nabagagem.connectbe.services.mappers;

import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.services.profile.ProfileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProfilePayloadMapper {
    private final ProfileMapper profileMapper;

    public ProfilePayload toPayload(ConnectProfile profile) {
        return toPayload(profile, false);
    }

    public ProfilePayload toPayload(ConnectProfile profile, Boolean includeAlts) {
        return new ProfilePayload(
                profile.getId(),
                Optional.ofNullable(profile.getParentProfile())
                        .map(ConnectProfile::getId)
                        .orElse(null),
                profile.getPersonalInfo(),
                Optional.ofNullable(profile.getProfileSkills())
                        .map(profileSkills -> profileSkills
                                .stream().map(profileMapper::toSkillReadPayload)
                                .collect(Collectors.toSet())).orElseGet(Collections::emptySet),
                profileMapper.toCertsPayload(
                        Optional.ofNullable(profile.getCertifications())
                                .orElseGet(Collections::emptySet)
                ),
                profile.getProfileBio(),
                profileMapper.toAvailPayload(profile.getAvailabilities()),
                profile.getProfileType(),
                includeAlts ? profile.getAltProfiles()
                        .stream().map(this::toPayload)
                        .collect(Collectors.toSet()) : Collections.emptySet()
        );
    }
}
