package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.CertificationPayload;

import java.util.Set;

public record CertificationsCommand(java.util.UUID id, Set<CertificationPayload> certifications) {
}
