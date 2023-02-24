package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "account", indexes = {
        @Index(columnList = "created_by"),
        @Index(columnList = "modified_by")
})
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @NotEmpty
    @Column(unique = true, nullable = false)
    private String userId;
    private String firstName;
    private String lastName;
    @OneToMany
    @RestResource
    @JoinColumn(name = "account_id")
    private Set<Address> addresses;
    @OneToMany(mappedBy = "account")
    @RestResource
    private Set<Gig> gigs;
    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();
    @RestResource
    @OneToMany
    @JoinColumn(name = "account_id")
    private Set<ConnectProfile> connectProfiles;
    @Embedded
    @Builder.Default
    private NotificationSettings notificationSettings = new NotificationSettings();
}