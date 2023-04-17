package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "certification", indexes = {
        @Index(name = "idx_certification_profile_id", columnList = "profile_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_certification_title", columnNames = {"title", "profile_id"})
})
@EqualsAndHashCode(of = "id")
public class Certification {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ConnectProfile profile;

    @NotBlank
    @Column(nullable = false)
    private String title;
    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer year;
}