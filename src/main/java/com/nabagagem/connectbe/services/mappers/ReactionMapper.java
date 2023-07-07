package com.nabagagem.connectbe.services.mappers;

import com.nabagagem.connectbe.domain.ThreadMessageReaction;
import com.nabagagem.connectbe.entities.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReactionMapper {
    @Mapping(target = "createdBy", source = "audit.createdBy")
    ThreadMessageReaction toDto(Reaction reaction);
}