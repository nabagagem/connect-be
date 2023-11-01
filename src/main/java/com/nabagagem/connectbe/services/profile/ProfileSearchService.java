package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchParams;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProfileSearchService {
    private final ProfileRepo profileRepo;
    private final KeywordService keywordService;
    private final ProfilePayloadMapper profileMapper;

    public Page<ProfileSearchItemPayload> searchFor(ProfileSearchParams profileSearchParams,
                                                    Pageable pageable) {
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
        Page<UUID> ids = profileRepo.searchIdsFor(
                workingModes,
                categories,
                keywords,
                keywords.isEmpty(),
                pageable
        );
        List<ConnectProfile> profileSearchItems = profileRepo.searchProfilesBy(ids.getContent());
        return new PageImpl<>(
                profileSearchItems.stream().map(profileMapper::toItemPayload).collect(Collectors.toList()),
                pageable,
                ids.getTotalElements()
        );
    }
}
