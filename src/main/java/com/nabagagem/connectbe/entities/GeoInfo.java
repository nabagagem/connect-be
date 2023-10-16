package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoInfo {
    private @NotNull double latitude;
    private @NotNull double longitude;
}
