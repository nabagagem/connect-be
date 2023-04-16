package com.nabagagem.connectbe.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "profile", indexes = {
        @Index(columnList = "address_id"),
        @Index(name = "idx_connectprofile_picture_id", columnList = "picture_id")
})
@EqualsAndHashCode(of = "id")
public class ConnectProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToOne
    @JoinColumn(name = "picture_id")
    private Media profilePicture;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<ProfileSkill> profileSkills;

    private String bio;

    private String language;

    @Embedded
    private ContactInfo contactInfo;

    @ManyToOne
    @RestResource
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToMany
    @RestResource
    private Set<Account> shares;
}