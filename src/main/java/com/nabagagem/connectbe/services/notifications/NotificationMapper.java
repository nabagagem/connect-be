package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.domain.NotificationItemPayload;
import com.nabagagem.connectbe.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    @Mapping(target = "targetProfile", source = "profile")
    Notification toEntity(NotificationCommand notificationCommand);

    NotificationItemPayload toDto(Notification notification);
}