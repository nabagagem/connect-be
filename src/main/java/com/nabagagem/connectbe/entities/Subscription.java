package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SubscriptionStatus status = SubscriptionStatus.APPROVAL_PENDING;
    @NotNull
    @Column(updatable = false)
    private String externalReference;
    @Builder.Default
    @ElementCollection
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Feature> features = Set.of(Feature.BOOST);
    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id", updatable = false)
    private Account account;
    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime startTime;
}