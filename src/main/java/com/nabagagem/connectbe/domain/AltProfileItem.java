package com.nabagagem.connectbe.domain;

import java.util.UUID;

public interface AltProfileItem {
    UUID getId();

    JobCategory getProfileCategory();

    String getProfession();

    Boolean getMainProfile();
}
