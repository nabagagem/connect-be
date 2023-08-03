package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.domain.notification.NotificationItemPayload;
import com.nabagagem.connectbe.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@SuppressWarnings("unused")
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    @Mapping(target = "targetProfile", source = "profile")
    @Mapping(target = "domainObject", qualifiedByName = "getDomainObjectFrom", source = "payload")
    Notification toEntity(NotificationCommand notificationCommand);

    NotificationItemPayload toDto(Notification notification);

    @Named("getDomainObjectFrom")
    default String getDomainObjectFrom(Object payload) {
        return Optional.ofNullable(payload)
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .orElse(null);
    }
}