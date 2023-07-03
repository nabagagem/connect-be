package com.nabagagem.connectbe.domain.commands;

import com.nabagagem.connectbe.domain.JobCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AltProfileCommand(
        @NotNull JobCategory profileCategory,
        @NotBlank String profession,
        @NotBlank String highlightTitle
) {
}
