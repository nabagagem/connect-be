package com.nabagagem.connectbe.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.nabagagem.connectbe.repos.ProfileSearchItem;

import java.util.Set;

public record ProfileSearchItemPayload(
        @JsonUnwrapped ProfileSearchItem profileSearchItem,
        Set<TopSkillPayload> topSkills
) {
}
