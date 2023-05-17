package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "bid", indexes = {
        @Index(name = "idx_bid_profile_id", columnList = "profile_id"),
        @Index(name = "idx_bid_target_job_id", columnList = "target_job_id"),
        @Index(name = "idx_bid_bidstatus", columnList = "bidStatus")
})
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Embedded
    private MoneyAmount budget;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "profile_id", nullable = false)
    private ConnectProfile owner;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "target_job_id", nullable = false)
    private Job targetJob;

    @Positive
    private Integer amountOfHours;

    @Size(max = 1000)
    @Column(length = 1000)
    private String experience;

    @Size(max = 1000)
    @Column(length = 1000)
    private String highlights;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BidStatus bidStatus = BidStatus.PENDING;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}