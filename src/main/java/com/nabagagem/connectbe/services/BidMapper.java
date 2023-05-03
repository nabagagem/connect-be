package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.BidCommand;
import com.nabagagem.connectbe.domain.BidPayload;
import com.nabagagem.connectbe.entities.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BidMapper {

    @Mapping(target = "owner", ignore = true)
    Bid toEntity(BidCommand bidCommand);

    @Mapping(target = "targetJobId", source = "targetJob.id")
    @Mapping(target = "targetJobTitle", source = "targetJob.title")
    BidPayload toDto(Bid bid);
}