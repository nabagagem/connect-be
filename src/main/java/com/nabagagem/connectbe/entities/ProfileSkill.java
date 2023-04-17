package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile_skill", indexes = {
        @Index(name = "idx_profileskill_skill_id", columnList = "skill_id"),
        @Index(name = "idx_profileskill_profile_id", columnList = "profile_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_profileskill_skill_id", columnNames = {"skill_id", "profile_id"})
})
@EqualsAndHashCode(of = "id")
public class ProfileSkill {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @NotNull
    @Column(nullable = false)
    private Integer certifications;
    @NotNull
    @Column(nullable = false)
    private SkillLevel level;
    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean top = false;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "profile_id", nullable = false)
    private ConnectProfile profile;

    public enum SkillLevel {
        ONE_2_THREE, THREE_2_FIVE, MORE_THAN_FIVE
    }
}