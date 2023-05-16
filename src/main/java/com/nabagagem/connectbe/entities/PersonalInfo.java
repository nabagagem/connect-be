package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.domain.WorkingMode;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {
    private @NotBlank String publicName;
    @NotBlank
    @Pattern(regexp = "[a-z0-9_-]+")
    @Column(unique = true)
    private String slug;
    private @NotBlank String profession;
    private @NotBlank String highlightTitle;
    @Enumerated(EnumType.STRING)
    private @NotNull ProfileCategory profileCategory;
    private @NotNull WorkingMode workingMode;
    private String city;
    private Boolean publicProfile;
    private Boolean available;
    @ElementCollection
    private Set<@NotBlank String> tags;
    @Embedded
    @Valid
    @NotNull
    private MoneyAmount amountPerHour;
}
