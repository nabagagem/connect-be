package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.CertificationPayload;

import java.util.Set;

public record CertificationsCommand(java.util.UUID id, Set<CertificationPayload> certifications) {
}
