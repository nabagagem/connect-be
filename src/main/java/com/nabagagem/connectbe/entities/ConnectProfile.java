package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile", indexes = {
        @Index(columnList = "account_id"),
        @Index(columnList = "address_id"),
        @Index(columnList = "account_id, language",
                unique = true, name = "uk_profile_language")
})
@EqualsAndHashCode(of = "id")
public class ConnectProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    private String bio;

    @NotEmpty
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