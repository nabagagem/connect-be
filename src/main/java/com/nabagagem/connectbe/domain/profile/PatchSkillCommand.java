package com.nabagagem.connectbe.domain.profile;

import java.util.UUID;

public record PatchSkillCommand(UUID id,
                                UUID skillId,
                                PatchSkillPayload patchSkillPayload) {
}
