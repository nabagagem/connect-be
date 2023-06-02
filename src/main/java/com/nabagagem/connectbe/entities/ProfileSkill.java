package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
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