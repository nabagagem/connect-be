package com.nabagagem.connectbe.services.mappers;

import com.nabagagem.connectbe.domain.profile.ProfileMediaItem;
import com.nabagagem.connectbe.entities.ProfileMedia;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMediaMapper {
    ProfileMedia toEntity(ProfileMediaItem profileMediaItem);

    @Mapping(target = "mediaType", source = "media.mediaType")
    @Mapping(target = "originalName", source = "media.originalName")
    ProfileMediaItem toDto(ProfileMedia profileMedia);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProfileMedia partialUpdate(ProfileMediaItem profileMediaItem, @MappingTarget ProfileMedia profileMedia);
}