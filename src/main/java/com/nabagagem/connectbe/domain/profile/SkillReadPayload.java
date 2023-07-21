package com.nabagagem.connectbe.domain.profile;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

public record SkillReadPayload(
        UUID id,
        @JsonUnwrapped SkillPayload skillPayload
) {
}
