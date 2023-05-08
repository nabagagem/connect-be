package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rating", indexes = {
        @Index(name = "idx_rating_target_profile_id", columnList = "target_profile_id"),
        @Index(name = "idx_rating_source_profile_id", columnList = "source_profile_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_rating_target_profile_id",
                columnNames = {"target_profile_id", "source_profile_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "target_profile_id", nullable = false)
    private ConnectProfile targetProfile;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "source_profile_id", nullable = false)
    private ConnectProfile sourceProfile;

    @Max(5)
    @Min(1)
    @NotNull
    private Integer stars;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}