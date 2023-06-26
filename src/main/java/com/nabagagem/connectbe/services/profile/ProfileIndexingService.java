package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ProfileIndexingService {
    private final ProfileService profileService;
    private final KeywordService keywordService;

    public void index(ConnectProfile profile) {
        String text = String.format(
                "%s %s %s %s %s %s %s %s",
                profile.getProfileSkills().stream().map(ProfileSkill::getSkill).map(Skill::getName)
                        .collect(Collectors.toSet()),
                profile.getPersonalInfo().getHighlightTitle(),
                profile.getPersonalInfo().getProfileCategory(),
                profile.getPersonalInfo().getTags(),
                profile.getPersonalInfo().getWorkingMode(),
                profile.getPersonalInfo().getCity(),
                profile.getPersonalInfo().getProfession(),
                profile.getCertifications().stream().map(Certification::getTitle).collect(Collectors.toSet())
        );
        log.info(text);
        Set<String> keywords = keywordService.extractFrom(
                text
        );
        log.info("Keywords: {}", keywords);
        profile.setKeywords(keywords);
        profileService.save(profile);
    }
}
