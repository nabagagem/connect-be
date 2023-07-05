package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSearchItem;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProfileSearchService {
    private final ProfileRepo profileRepo;
    private final KeywordService keywordService;

    public Page<ProfileSearchItemPayload> searchFor(ProfileSearchParams profileSearchParams,
                                                    UUID loggedUserId, Pageable pageable) {
        Set<WorkingMode> workingModes = Optional.ofNullable(profileSearchParams.workingMode())
                .filter(w -> !w.isEmpty())
                .orElseGet(() -> Set.of(WorkingMode.values()));
        Set<JobCategory> categories = Optional.ofNullable(profileSearchParams.category())
                .filter(c -> !c.isEmpty())
                .orElseGet(() -> Set.of(JobCategory.values()));
        Set<String> keywords = Optional.ofNullable(profileSearchParams.searchExpression())
                .filter(StringUtils::isNotBlank)
                .map(keywordService::extractFrom)
                .orElse(Set.of());
        Page<String> ids = profileRepo.searchIdsFor(
                workingModes,
                categories,
                keywords,
                keywords.isEmpty(),
                loggedUserId,
                pageable
        );
        List<ProfileSearchItem> profileSearchItems = profileRepo.profileSearch(ids.getContent());
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
