package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
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
    private String description;
    private String professionalRecord;
}
