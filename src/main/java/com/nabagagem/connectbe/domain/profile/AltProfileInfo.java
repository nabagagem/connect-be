package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.entities.Language;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.entities.ProfileSkill;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Projection for {@link com.nabagagem.connectbe.entities.ConnectProfile}
 */
public interface AltProfileInfo {
    UUID getId();

    PersonalInfoInfo getPersonalInfo();

    Set<ProfileSkillInfo> getProfileSkills();

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.PersonalInfo}
     */
    interface PersonalInfoInfo {
        String getPublicName();

        String getSlug();

        String getProfession();

        String getHighlightTitle();

        JobCategory getProfileCategory();

        String getOtherCategory();

        WorkingMode getWorkingMode();

        String getCity();

        Boolean getPublicProfile();

        Boolean getAvailable();

        Language getLanguage();

        MoneyAmountInfo getAmountPerHour();

        /**
         * Projection for {@link com.nabagagem.connectbe.entities.MoneyAmount}
         */
        interface MoneyAmountInfo {
            BigDecimal getAmount();

            MoneyAmount.MoneyCurrency getCurrency();
        }
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
}