package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.LinkType;

import java.util.Map;
import java.util.UUID;

public record ProfileLinksCommand(UUID id, Map<LinkType, String> links) {
}
