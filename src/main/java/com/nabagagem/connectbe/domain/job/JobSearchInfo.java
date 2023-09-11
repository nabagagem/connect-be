package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.FilePurpose;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.entities.ProfileType;
import org.springframework.http.MediaType;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Projection for {@link com.nabagagem.connectbe.entities.Job}
 */
public interface JobSearchInfo {
    UUID getId();

    String getTitle();

    JobCategory getJobCategory();

    String getDescription();

    JobSize getJobSize();

    JobFrequency getJobFrequency();

    String getBackground();

    JobMode getJobMode();

    JobRequiredAvailability getRequiredAvailability();

    String getAddress();

    String getAddressReference();

    JobStatus getJobStatus();

    Set<String> getTags();

    Set<String> getKeywords();

    Set<SkillInfo> getRequiredSkills();

    ConnectProfileInfo getOwner();

    Set<JobMediaInfo> getJobMedia();

    AuditInfo getAudit();

    MoneyAmount getBudget();

    DateInterval getRequiredDates();

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Skill}
     */
    interface SkillInfo {
        String getName();
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.ConnectProfile}
     */
    interface ConnectProfileInfo {
        UUID getId();

        ZonedDateTime getLastActivity();

        ProfileType getProfileType();

        PersonalInfoInfo getPersonalInfo();

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.PersonalInfo}
         */
        interface PersonalInfoInfo {
            String getPublicName();

            String getSlug();

            Boolean getPublicProfile();
        }
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.JobMedia}
     */
    interface JobMediaInfo {
        UUID getId();

        FilePurpose getFilePurpose();

        Integer getPosition();

        MediaInfo getMedia();

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.Media}
         */
        interface MediaInfo {
            String getOriginalName();

            MediaType getMediaType();

            String getFileUrl();

            String getDescription();
        }
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Audit}
     */
    interface AuditInfo {
        ZonedDateTime getCreatedAt();

        ZonedDateTime getModifiedAt();
    }
}