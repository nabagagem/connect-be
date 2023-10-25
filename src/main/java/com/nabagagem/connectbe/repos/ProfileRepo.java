package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.AltProfileInfo;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.GdprLevel;
import com.nabagagem.connectbe.entities.ProfileType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@JaversSpringDataAuditable
public interface ProfileRepo extends
        CrudRepository<ConnectProfile, UUID> {
    @Query("""
                select p.id from ConnectProfile p
                where p.personalInfo.slug = :slug
            """)
    Optional<UUID> findIdFromSlug(String slug);

    @Modifying
    @Query("update ConnectProfile p set p.lastActivity = :lastActivity where p.id = :id")
    void updateLastActivityFor(UUID id, ZonedDateTime lastActivity);

    @Query("""
                select p.id from ConnectProfile p
                    left join p.profileSkills s
                    left join p.keywords k
                where (p.personalInfo.workingMode in (:workingModes))
                  and (p.personalInfo.profileCategory in (:categories))
                  and p.personalInfo.publicProfile = true
                  and (:invKeywords = true or k in (:keywords))
                group by p.id
            """)
    Page<String> searchIdsFor(Set<WorkingMode> workingModes,
                              Set<JobCategory> categories,
                              Set<String> keywords,
                              Boolean invKeywords, Pageable pageable);

    @Query(value = """
                 select profile.*, s.name as skillName, ps.level as skillLevel
            from (select p.id,
                          p.public_name as publicName,
                          p.created_at as firstLogin,
                          p.available,
                          p.city,
                          p.slug as slug,
                          p.public_profile as publicProfile,
                          p.highlight_title as highlight,
                          p.profession,
                          p.working_mode as workingMode,
                          p.profile_category as category,
                          p.language as language,
                          count(alljobs.id)     as publishedJobs,
                          count(finishedBid.id) as finishedBids,
                          count(j.id)           as finishedJobs,
                          count(r.id)           as ratings,
                          avg(r.stars)          as stars
                   from profile p
                       left join bid finishedBid
                         on p.id = finishedBid.profile_id
                          and finishedBid.bid_status = 'APPROVED'
                       left join job j
                         on p.id = j.owner_id
                          and j.job_status = 'FINISHED'
                       left join job alljobs
                          on p.id = alljobs.owner_id
                       left join rating r
                         on p.id = r.target_profile_id
                   where p.id in (:ids)
                   group by p.id, p.public_name,
                            p.created_at, p.available,
                            p.city, p.highlight_title,
                            p.profession, p.working_mode,p.language,
                            p.profile_category) as profile
                   left join profile_skill ps
                      left join skill s on ps.skill_id = s.id
                      on profile.id = ps.profile_id
                        and ps.top = true
                """, nativeQuery = true)
    List<ProfileSearchItem> profileSearch(List<String> ids);

    boolean existsByPersonalInfoEmail(String email);

    @Query("""
                select count(main)>0
                    from ConnectProfile alt
                    inner join alt.parentProfile main
                where alt.id = :altProfileId
                and main.id = :mainProfileId
            """)
    boolean isAltFrom(UUID altProfileId, UUID mainProfileId);

    @Query("""
                select p from ConnectProfile p
                    left join fetch p.profilePicture
                    left join fetch p.parentProfile
                    left join fetch p.profileSkills ps
                        left join fetch ps.skill
                    left join fetch p.personalInfo
                    where p.parentProfile.id = :profileId
            """)
    List<AltProfileInfo> listAltProfilesFor(UUID profileId);

    @Query("""
                select p from ConnectProfile p
                    left join p.altProfiles alt
                where (p.parentProfile is null and p.id = :profileId)
                    or alt.id = :profileId
            """)
    Optional<ConnectProfile> findParentFrom(UUID profileId);

    @Query("""
                select p.profileType
                from ConnectProfile p
                where p.id = :id
            """)
    Optional<ProfileType> getProfileTypeFor(UUID id);

    @Query("""
                select p from ConnectProfile p
                    left join fetch p.personalInfo.tags
                    left join fetch p.altProfiles
                    left join fetch p.parentProfile
                    left join fetch p.availabilities
                    left join fetch p.certifications
                    left join fetch p.profileSkills ps
                        left join fetch ps.skill
                    left join fetch p.profilePicture
                where p.id = :id
            """)
    Optional<ConnectProfile> findForProfileRead(UUID id);

    @Query("""
                select p.personalInfo.publicName
                from ConnectProfile p
                where p.id = :id
            """)
    Optional<String> getNameFrom(UUID id);

    @Query("""
                select g.gdprLevels
                from ConnectProfile p
                    inner join p.gdpr g
                where p.id = :profileId
            """)
    Set<GdprLevel> findGdprFrom(UUID profileId);

    @Query("""
                select p from ConnectProfile p
                    left join fetch p.personalInfo.tags 
                    left join fetch p.profilePicture
                    left join fetch p.gdpr gdpr
                        left join fetch gdpr.gdprLevels
                    left join fetch p.profileSkills ps
                        left join fetch ps.skill
                    left join fetch p.availabilities
                    left join fetch p.certifications
                    left join fetch p.altProfiles alt
                        left join fetch alt.personalInfo.tags
                        left join fetch alt.profilePicture
                        left join fetch alt.profileSkills aps
                            left join fetch aps.skill
                        left join fetch alt.availabilities
                        left join fetch alt.certifications
                where p.id = :profileId
            """)
    ConnectProfile getMyProfile(UUID profileId);
}
