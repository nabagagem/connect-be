package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
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
    private final KeywordService keywordService;

    public Set<String> extractFrom(ConnectProfile profile) {
        log.info("Indexing profile: {}", profile.getId());
        Optional<PersonalInfo> personalInfo = Optional.ofNullable(profile.getPersonalInfo());
        String text = String.format(
                "%s %s %s %s %s %s %s %s",
                listOrEmpty(Optional.ofNullable(profile.getProfileSkills())
                        .orElseGet(Set::of)
                        .stream().map(ProfileSkill::getSkill).map(Skill::getName)
                        .collect(Collectors.toSet())),
                nullOrEmpty(personalInfo.map(PersonalInfo::getHighlightTitle).orElse(null)),
                nullOrEmpty(personalInfo.map(PersonalInfo::getProfileCategory).orElse(null)),
                listOrEmpty(personalInfo.map(PersonalInfo::getTags).orElse(null)),
                nullOrEmpty(personalInfo.map(PersonalInfo::getWorkingMode).orElse(null)),
                nullOrEmpty(personalInfo.map(PersonalInfo::getCity).orElse(null)),
                nullOrEmpty(personalInfo.map(PersonalInfo::getProfession).orElse(null)),
                Optional.ofNullable(profile.getCertifications()).orElse(Set.of())
                        .stream().map(Certification::getTitle).collect(Collectors.toSet())
        );
        Set<String> keywords = keywordService.extractFrom(
                text
        );
        log.info("Keywords: {}", keywords);
        return keywords;
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
