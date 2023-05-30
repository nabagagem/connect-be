package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileBio {
    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Size(max = 1000)
    @Column(length = 1000)
    private String professionalRecord;
}
