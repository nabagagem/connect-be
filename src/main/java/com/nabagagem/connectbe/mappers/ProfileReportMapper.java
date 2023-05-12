package com.nabagagem.connectbe.mappers;

import com.nabagagem.connectbe.domain.CreateProfileReportPayload;
import com.nabagagem.connectbe.domain.ReportItemPayload;
import com.nabagagem.connectbe.entities.ProfileReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileReportMapper {
    ProfileReport toEntity(CreateProfileReportPayload createProfileReportPayload);

    @Mapping(target = "targetProfileId", source = "targetProfile.id")
    @Mapping(target = "targetProfileName", source = "targetProfile.personalInfo.publicName")
    @Mapping(target = "reporterProfileId", source = "reporter.id")
    @Mapping(target = "reporterProfileName", source = "reporter.personalInfo.publicName")
    @Mapping(target = "targetJobId", source = "targetJob.id")
    @Mapping(target = "targetJobTitle", source = "targetJob.title")
    ReportItemPayload toItemPayload(ProfileReport profileReport);
}