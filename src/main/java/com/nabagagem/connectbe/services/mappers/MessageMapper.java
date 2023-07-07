package com.nabagagem.connectbe.services.mappers;

import com.nabagagem.connectbe.domain.ThreadMessage;
import com.nabagagem.connectbe.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ReactionMapper.class})
public interface MessageMapper {
    ThreadMessage toDto(Message message);
}