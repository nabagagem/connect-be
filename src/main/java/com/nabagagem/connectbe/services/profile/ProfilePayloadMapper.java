package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.profile.TopSkillPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileSkill;
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
                profileMapper.toLinksMap(profile.getProfileLinks()),
                new ProfileMetrics(profile.getLastActivity(), profile.getAudit().getCreatedAt()),
                profile.getProfileType()
        );
    }

    public ProfileSearchItemPayload toItemPayload(ConnectProfile profile) {
        return new ProfileSearchItemPayload(
                toPayload(profile),
                profile.getProfileSkills()
                        .stream().filter(ProfileSkill::getTop)
                        .map(profileSkill -> new TopSkillPayload(
                                profileSkill.getLevel().toString(),
                                profileSkill.getSkill().getName()
                        )).collect(Collectors.toSet())
        );
    }
}
