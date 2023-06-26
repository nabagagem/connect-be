package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
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
                listOrEmpty(profile.getProfileSkills().stream().map(ProfileSkill::getSkill).map(Skill::getName)
                        .collect(Collectors.toSet())),
                nullOrEmpty(profile.getPersonalInfo().getHighlightTitle()),
                nullOrEmpty(profile.getPersonalInfo().getProfileCategory()),
                listOrEmpty(profile.getPersonalInfo().getTags()),
                nullOrEmpty(profile.getPersonalInfo().getWorkingMode()),
                nullOrEmpty(profile.getPersonalInfo().getCity()),
                nullOrEmpty(profile.getPersonalInfo().getProfession()),
                profile.getCertifications().stream().map(Certification::getTitle).collect(Collectors.toSet())
        );
        Set<String> keywords = keywordService.extractFrom(
                text
        );
        log.info("Keywords: {}", keywords);
        profile.setKeywords(keywords);
        profileService.save(profile);
    }

    private String listOrEmpty(Collection<String> strings) {
        return Optional.ofNullable(strings)
                .filter(__ -> !strings.isEmpty())
                .map(Objects::toString)
                .orElse("");
    }

    private String nullOrEmpty(Object object) {
        return Optional.ofNullable(object)
                .map(Object::toString)
                .orElse("");
    }
}
