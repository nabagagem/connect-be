package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "profile")
@EqualsAndHashCode(of = "id")
public class ConnectProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id")
    private Media profilePicture;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<ProfileSkill> profileSkills;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<Certification> certifications;

    private String bio;

    private String language;

    @Embedded
    private ContactInfo contactInfo;
}