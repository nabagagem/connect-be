package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@Table(name = "message", indexes = {
        @Index(name = "idx_message_created_at", columnList = "created_at"),
        @Index(name = "idx_message_thread_id", columnList = "thread_id"),
        @Index(name = "idx_message_media_id", columnList = "media_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;

    @Column(length = 1000)
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_id")
    private Media media;

    @NotNull
    @Builder.Default
    private Boolean read = false;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}