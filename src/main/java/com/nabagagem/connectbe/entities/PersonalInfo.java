package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private @NotBlank @Size(max = 200) String publicName;
    @NotBlank
    @Column(unique = true)
    private String slug;
    private @NotBlank @Size(max = 100) String profession;
    private @NotBlank @Size(max = 100) String highlightTitle;

    @Enumerated(EnumType.STRING)
    private @NotNull JobCategory profileCategory;

    @Size(max = 100)
    private String otherCategory;

    @Enumerated(EnumType.STRING)
    private @NotNull WorkingMode workingMode;
    private @Size(max = 100) String city;
    private Boolean publicProfile;
    private Boolean available;
    @ElementCollection
    private @Size(max = 10) Set<@NotBlank String> tags;
    @Embedded
    @Valid
    @NotNull
    private MoneyAmount amountPerHour;
    @Email
    @Column(unique = true)
    private @Size(max = 100) String email;
    private Boolean enableMessageEmail;
}
