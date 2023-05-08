package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification", columnList = "target_profile_id"),
        @Index(name = "idx_notification_type", columnList = "type")
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
    private NotificationType type;

    private String targetObjectId;

    @Builder.Default
    private Boolean read = false;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}