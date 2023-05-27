package com.nabagagem.connectbe.mappers;

import com.nabagagem.connectbe.domain.ProfileRatingPayload;
import com.nabagagem.connectbe.domain.RatingPayload;
import com.nabagagem.connectbe.entities.Rating;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URL;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    Rating toEntity(RatingPayload ratingPayload);

    RatingPayload toDto(Rating rating);

    @Mapping(target = "ratedAt", source = "audit.createdAt")
    @Mapping(target = "sourceProfileId", source = "sourceProfile.id")
    @Mapping(target = "sourceProfilePicURL", source = ".")
    @Mapping(target = "sourceProfilePublicName", source = "sourceProfile.personalInfo.publicName")
    ProfileRatingPayload toProfileDto(Rating rating);

    @SneakyThrows
    default URL picURLFrom(Rating rating) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/profile/{id}/pic")
                .build(rating.getSourceProfile().getId())
                .toURL();
    }
}