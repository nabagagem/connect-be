package com.nabagagem.connectbe.services.mappers;

import com.nabagagem.connectbe.domain.messages.ThreadMessage;
import com.nabagagem.connectbe.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ReactionMapper.class})
public interface MessageMapper {

    @Mapping(target = "sentAt", source = "audit.createdAt")
    @Mapping(target = "sentBy", source = "audit.createdBy")
    @Mapping(target = "message", source = "text")
    @Mapping(target = "mediaType", source = "media.mediaType")
    @Mapping(target = "mediaOriginalName", source = "media.originalName")
    @Mapping(target = "threadId", source = "thread.id")
    ThreadMessage toDto(Message message);
}