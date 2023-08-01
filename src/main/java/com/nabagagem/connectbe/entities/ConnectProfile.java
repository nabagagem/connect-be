package com.nabagagem.connectbe.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile")
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class ConnectProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id")
    private Media profilePicture;

    @OneToMany(mappedBy = "parentProfile")
    private Set<ConnectProfile> altProfiles;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ConnectProfile parentProfile;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<ProfileSkill> profileSkills;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<Certification> certifications;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<Availability> availabilities;

    @Embedded
    private ProfileBio profileBio;

    private ZonedDateTime lastActivity;

    @ElementCollection
    private Set<@NotBlank String> keywords;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

    @Override
    public String toString() {
        return "ConnectProfile{" +
                "id=" + id +
                ", personalInfo=" + personalInfo +
                '}';
    }
}