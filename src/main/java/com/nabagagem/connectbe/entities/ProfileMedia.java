package com.nabagagem.connectbe.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "profile_media", indexes = {
        @Index(name = "idx_profilemedia_profile_id", columnList = "profile_id"),
        @Index(name = "idx_profilemedia_media_id", columnList = "media_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_profilemedia_media_id", columnNames = {"media_id", "profile_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class ProfileMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "profile_id", nullable = false)
    private ConnectProfile profile;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}