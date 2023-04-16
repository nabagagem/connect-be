package com.nabagagem.connectbe.mappers;

import com.nabagagem.connectbe.domain.PersonalInfoCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileCommandMapper {
    ConnectProfile toEntity(PersonalInfoCommand personalInfoCommand);
}
