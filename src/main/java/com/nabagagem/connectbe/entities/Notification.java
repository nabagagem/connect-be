package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.services.notifications.Action;
import jakarta.persistence.Column;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification", columnList = "target_profile_id")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "target_profile_id", nullable = false)
    private ConnectProfile targetProfile;

    @NotEmpty
    private String title;

    @Enumerated(EnumType.STRING)
    private Action action;

    private String domainObject;

    private String targetObjectId;

    @Builder.Default
    private Boolean read = false;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}