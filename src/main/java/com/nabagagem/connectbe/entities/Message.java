package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @Index(name = "idx_message_thread_id", columnList = "thread_id")
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

    @NotNull
    @Size(min = 10, max = 1000)
    @Column(length = 1000, nullable = false)
    private String text;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}