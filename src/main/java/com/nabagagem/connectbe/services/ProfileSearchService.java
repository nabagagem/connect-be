package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.ProfileSearchParams;
import com.nabagagem.connectbe.domain.TopSkillPayload;
import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.ProfileCategory;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSearchItem;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProfileSearchService {
    private final ProfileRepo profileRepo;

    public Page<ProfileSearchItemPayload> searchFor(ProfileSearchParams profileSearchParams,
                                                    Pageable pageable) {
        Page<String> ids = profileRepo.searchIdsFor(
                Optional.ofNullable(profileSearchParams.workingMode())
                        .filter(workingModes -> !workingModes.isEmpty())
                        .orElseGet(() -> Set.of(WorkingMode.values())),
                Optional.ofNullable(profileSearchParams.category())
                        .filter(categories -> !categories.isEmpty())
                        .orElseGet(() -> Set.of(ProfileCategory.values())),
                Optional.ofNullable(profileSearchParams.searchExpression())
                        .map(String::trim)
                        .filter(StringUtils::isNotBlank)
                        .orElse(null),
                pageable
        );
        List<ProfileSearchItem> profileSearchItems = profileRepo.profileSearch(ids.getContent(),
                pageable);
        return new PageImpl<>(
                toPayloadResponse(profileSearchItems),
                pageable,
                ids.getTotalElements()
        );
    }

    private List<ProfileSearchItemPayload> toPayloadResponse(List<ProfileSearchItem> profileSearchItems) {
        return profileSearchItems.stream()
                .collect(Collectors.groupingBy(ProfileSearchItem::getId))
                .values()
                .stream()
                .map(searchItems -> new ProfileSearchItemPayload(
                        searchItems.stream().findAny().orElse(null),
                        searchItems.stream()
                                .filter(Objects::nonNull)
                                .filter(profileSearchItem -> StringUtils.isNotBlank(profileSearchItem.getSkillName()))
                                .map(profileSearchItem -> new TopSkillPayload(
                                        profileSearchItem.getSkillLevel(),
                                        profileSearchItem.getSkillName()))
                                .collect(Collectors.toSet())
                )).collect(Collectors.toList());
    }
}
