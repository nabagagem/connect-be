package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import static com.nabagagem.connectbe.services.notifications.MessageGateway.WsNotificationPayload;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface WsPayloadMapper {
    WsNotificationPayload toWsPayload(NotificationCommand notificationCommand);
}
