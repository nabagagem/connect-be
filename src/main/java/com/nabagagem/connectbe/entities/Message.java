package com.nabagagem.connectbe.entities;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Table(name = "message", indexes = {
        @Index(name = "idx_message_created_at", columnList = "created_at"),
        @Index(name = "idx_message_created_by", columnList = "created_by"),
        @Index(name = "idx_message_read", columnList = "read"),
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

    @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE)
    private Set<Reaction> reactions;

    @ElementCollection
    private Set<String> keywords;

    @Builder.Default
    private Boolean textUpdated = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

}