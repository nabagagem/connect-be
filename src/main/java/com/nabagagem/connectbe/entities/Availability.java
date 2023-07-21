package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "availability", indexes = {
        @Index(name = "idx_availability_profile_id", columnList = "profile_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_availability_dayofweek", columnNames = {"dayOfWeek", "profile_id"})
})
@EqualsAndHashCode(of = "id")
public class Availability {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "profile_id", nullable = false)
    private ConnectProfile profile;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AvailabilityType availabilityType;

}