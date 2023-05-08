package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@Table(name = "thread", indexes = {
        @Index(name = "idx_thread_sender_id", columnList = "sender_id"),
        @Index(name = "idx_thread_recipient_id", columnList = "recipient_id"),
        @Index(name = "idx_thread_created_at", columnList = "created_at"),
        @Index(name = "idx_thread_last_message_id", columnList = "last_message_id"),
        @Index(name = "idx_thread_bid_id", columnList = "bid_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "sender_id", nullable = false)
    private ConnectProfile sender;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "recipient_id", nullable = false)
    private ConnectProfile recipient;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @NotNull
    @PastOrPresent
    private LocalDateTime lastMessageAt;

    @ManyToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @OneToMany(mappedBy = "thread")
    private Set<Message> messages;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                '}';
    }
}