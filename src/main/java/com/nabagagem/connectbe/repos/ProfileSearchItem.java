package com.nabagagem.connectbe.repos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.WorkingMode;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ProfileSearchItem {
    UUID getId();

    String getSlug();

    String getPublicName();

    Boolean getAvailable();

    JobCategory getCategory();

    WorkingMode getWorkingMode();

    boolean getPublicProfile();

    String getCity();

    Instant getFirstLogin();

    Long getFinishedJobs();

    Long getPublishedJobs();

    Long getFinishedBids();

    Double getStars();

    @JsonIgnore
    String getSkillName();

    @JsonIgnore
    String getSkillLevel();

    String getProfession();

    String getHighlight();

    Long getRatings();

    Locale getLanguage();
}
