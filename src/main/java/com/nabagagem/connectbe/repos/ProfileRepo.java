package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.WorkingMode;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileCategory;
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
                where (p.personalInfo.workingMode in (:workingModes))
                  and (p.personalInfo.profileCategory in (:categories))
                  and (
                       :expression is null or
                       p.profileBio.description like %:expression% or
                       p.profileBio.professionalRecord like %:expression% or
                       p.personalInfo.publicName like %:expression%
                  )
                group by p.id
            """)
    Page<String> searchIdsFor(Set<WorkingMode> workingModes,
                              Set<ProfileCategory> categories,
                              String expression,
                              Pageable pageable);

    @Query(value = """
                 select profile.*, s.name as skillName, ps.level as skillLevel
            from (select p.id,
                          p.public_name as publicName,
                          p.created_at as firstLogin,
                          p.available,
                          p.city,
                          p.highlight_title as highlight,
                          p.profession,
                          p.working_mode as workingMode,
                          p.profile_category as category,
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
                       left join rating r 
                         on p.id = r.target_profile_id
                   where p.id in (:ids)
                   group by p.id, p.public_name,
                            p.created_at, p.available,
                            p.city, p.highlight_title,
                            p.profession, p.working_mode,
                            p.profile_category) as profile
                   left join profile_skill ps
                      left join skill s on ps.skill_id = s.id
                      on profile.id = ps.profile_id
                        and ps.top = true
                """, nativeQuery = true)
    List<ProfileSearchItem> profileSearch(List<String> ids, Pageable pageable);
}
