package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.PersonalInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PersonalInfoCommand(java.util.UUID id,
                                  @NotNull @Valid PersonalInfo personalInfo) {
}
