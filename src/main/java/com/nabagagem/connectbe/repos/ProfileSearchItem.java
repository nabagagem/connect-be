package com.nabagagem.connectbe.repos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.ProfileCategory;

import java.time.Instant;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ProfileSearchItem {
    UUID getId();

    String getPublicName();

    Boolean getAvailable();

    ProfileCategory getCategory();

    WorkingMode getWorkingMode();

    String getCity();

    Instant getFirstLogin();

    Long getFinishedJobs();

    Long getFinishedBids();

    Double getStars();

    @JsonIgnore
    String getSkillName();

    @JsonIgnore
    String getSkillLevel();

    String getProfession();

    String getHighlight();

    Long getRatings();
}
