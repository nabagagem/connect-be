package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.GdprLevel;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Projection for {@link com.nabagagem.connectbe.entities.ConnectProfile}
 */
public interface MyProfileInfo {
    UUID getId();

    ZonedDateTime getLastActivity();

    ProfileType getProfileType();

    PersonalInfo getPersonalInfo();

    MediaInfo getProfilePicture();

    Set<AltProfileInfo> getAltProfiles();

    Set<ProfileSkillInfo> getProfileSkills();

    Set<CertificationInfo> getCertifications();

    Set<AvailabilityInfo> getAvailabilities();

    ProfileBioInfo getProfileBio();

    GdprInfo getGdpr();

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.PersonalInfo}
     */
    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Media}
     */
    interface MediaInfo {
        UUID getId();

        String getOriginalName();

        MediaType getMediaType();

        String getFileUrl();

        String getDescription();
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.ConnectProfile}
     */
    interface AltProfileInfo {
        UUID getId();

        ZonedDateTime getLastActivity();

        ProfileType getProfileType();

        PersonalInfo getPersonalInfo();

        MediaInfo getProfilePicture();

        Set<ProfileSkillInfo> getProfileSkills();

        Set<CertificationInfo> getCertifications();

        Set<AvailabilityInfo> getAvailabilities();

        ProfileBioInfo getProfileBio();

        interface MediaInfo {
            UUID getId();

            String getOriginalName();

            MediaType getMediaType();

            String getFileUrl();

            String getDescription();
        }

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.ProfileSkill}
         */
        interface ProfileSkillInfo {
            UUID getId();

            Integer getCertifications();

            ProfileSkill.SkillLevel getLevel();

            Boolean getTop();

            SkillInfo getSkill();

            /**
             * Projection for {@link com.nabagagem.connectbe.entities.Skill}
             */
            interface SkillInfo {
                UUID getId();

                String getName();
            }
        }

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.Certification}
         */
        interface CertificationInfo {
            UUID getId();

            String getTitle();

            Integer getYear();
        }

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.Availability}
         */
        interface AvailabilityInfo {
            UUID getId();

            DayOfWeek getDayOfWeek();

            AvailabilityType getAvailabilityType();
        }

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.ProfileBio}
         */
        interface ProfileBioInfo {
            String getDescription();

            String getProfessionalRecord();
        }
    }

    /**
     * Projection for {@link ProfileSkill}
     */
    interface ProfileSkillInfo {
        UUID getId();

        Integer getCertifications();

        ProfileSkill.SkillLevel getLevel();

        Boolean getTop();

        SkillInfo getSkill();

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.Skill}
         */
        interface SkillInfo {
            UUID getId();

            String getName();
        }
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Certification}
     */
    interface CertificationInfo {
        UUID getId();

        String getTitle();

        Integer getYear();
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Availability}
     */
    interface AvailabilityInfo {
        UUID getId();

        DayOfWeek getDayOfWeek();

        AvailabilityType getAvailabilityType();
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.ProfileBio}
     */
    interface ProfileBioInfo {
        String getDescription();

        String getProfessionalRecord();
    }

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Gdpr}
     */
    interface GdprInfo {
        Set<GdprLevel> getGdprLevels();
    }
}