package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

    @OneToOne
    @NotNull
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}