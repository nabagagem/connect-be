package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@Valid
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @OneToMany
    @JoinColumn(name = "account_id")
    private Set<Address> addresses;
    @OneToMany(mappedBy = "account")
    private Set<Gig> gigs;
    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();
    @Embedded
    @Builder.Default
    private NotificationSettings notificationSettings = new NotificationSettings();
}