package com.nabagagem.connectbe.mappers;

import com.nabagagem.connectbe.domain.ProfileRatingPayload;
import com.nabagagem.connectbe.domain.RatingPayload;
import com.nabagagem.connectbe.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    Rating toEntity(RatingPayload ratingPayload);

    RatingPayload toDto(Rating rating);

    @Mapping(target = "ratedAt", source = "audit.createdAt")
    @Mapping(target = "sourceProfileId", source = "sourceProfile.id")
    @Mapping(target = "sourceProfilePublicName", source = "sourceProfile.personalInfo.publicName")
    ProfileRatingPayload toProfileDto(Rating rating);
}