package com.nabagagem.connectbe.domain.profile;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Set;

public record ProfileSearchItemPayload(
        @JsonUnwrapped ProfilePayload profilePayload,
        Set<TopSkillPayload> topSkills
) {
}
