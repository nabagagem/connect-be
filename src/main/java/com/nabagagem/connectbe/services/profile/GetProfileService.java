package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.rating.RatingListService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GetProfileService {
    private final ProfileRepo profileRepo;
    private final RatingListService ratingListService;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final ProfileMetricsService profileMetricsService;

    public ProfilePayload getProfile(UUID id, UUID loggedUserId) {
        ConnectProfile profile = profileRepo.findForProfileRead(id)
                .orElseGet(() -> Optional.ofNullable(loggedUserId)
                        .filter(id::equals)
                        .map(profileService::init)
                        .orElseThrow(ProfileNotFoundException::new));
        return new ProfilePayload(
                profile.getId(),
                Optional.ofNullable(profile.getParentProfile())
                        .map(ConnectProfile::getId)
                        .orElse(null),
                profile.getPersonalInfo(),
                ratingListService.getAverageFor(id),
                Optional.ofNullable(profile.getProfileSkills())
                        .map(profileSkills -> profileSkills
                                .stream().map(profileMapper::toSkillReadPayload)
                                .collect(Collectors.toSet())).orElseGet(Collections::emptySet),
                profileMapper.toCertsPayload(
                        Optional.ofNullable(profile.getCertifications())
                                .orElseGet(Collections::emptySet)
                ),
                profileMetricsService.getMetricsFor(id).orElse(null),
                profile.getProfileBio(),
                profileMapper.toAvailPayload(profile.getAvailabilities()),
                Optional.ofNullable(loggedUserId)
                        .flatMap(__ -> ratingListService.findRatingsFromTo(loggedUserId, id))
                        .orElse(null),
                ratingListService.findRatingsFor(id, Pageable.ofSize(5)).getContent(),
                profile.getProfileType()
        );
    }
}
