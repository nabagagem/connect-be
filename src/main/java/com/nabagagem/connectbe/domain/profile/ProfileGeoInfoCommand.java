package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.GeoInfo;

import java.util.UUID;

public record ProfileGeoInfoCommand(UUID profileId, GeoInfo geoInfo) {
}
