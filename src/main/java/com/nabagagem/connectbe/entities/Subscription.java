package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "subscription", indexes = {
        @Index(name = "idx_subscription_account_id", columnList = "account_id")
})
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.APPROVAL_PENDING;
    @ElementCollection
    private Set<Feature> features;
    @ManyToOne
    @RestResource
    @JoinColumn(name = "account_id")
    private Account account;
    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
    private LocalDateTime startTime;
}