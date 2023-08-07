package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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
@Audited
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