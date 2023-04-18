package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.PersonalInfo;

import java.util.Set;

public record ProfilePayload(
        PersonalInfo personalInfo,
        Set<SkillPayload> skills,
        Set<CertificationPayload> certifications
) {
}
