package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.CertificationPayload;

import java.util.Set;

public record CertificationsCommand(String id, Set<CertificationPayload> certifications) {
}
