package com.nabagagem.connectbe.mappers;

import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.repos.ProfileSearchItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileSearchMapper {
    ProfileSearchItemPayload toDto(ProfileSearchItem profileSearchItem);
}