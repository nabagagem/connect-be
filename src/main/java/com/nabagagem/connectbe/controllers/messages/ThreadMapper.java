package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.MessageThread;
import com.nabagagem.connectbe.entities.Thread;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThreadMapper {
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "recipientName", source = "recipient.personalInfo.publicName")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.personalInfo.publicName")
    @Mapping(target = "lastMessageText", source = "lastMessage.text")
    @Mapping(target = "lastModifiedBy", source = "audit.modifiedBy")
    MessageThread toDto(Thread thread);
}