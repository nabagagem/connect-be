package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.PersonalInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PersonalInfoCommand(@NotEmpty String id,
                                  @NotNull @Valid PersonalInfo personalInfo) {
}
