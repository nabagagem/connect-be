package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;

import java.util.UUID;

public interface AltProfileItem {
    UUID getId();

    JobCategory getProfileCategory();

    String getProfession();

    Boolean getMainProfile();
}
