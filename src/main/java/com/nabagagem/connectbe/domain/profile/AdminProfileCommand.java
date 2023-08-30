package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.ProfileType;
import jakarta.validation.constraints.NotNull;

public record AdminProfileCommand(
        @NotNull ProfileType profileType
) {
}
