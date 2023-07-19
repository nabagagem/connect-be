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
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
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
        @Index(name = "idx_thread_bid_id", columnList = "bid_id"),
        @Index(name = "idx_thread_status", columnList = "status")
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
    private ZonedDateTime lastMessageAt;

    @ManyToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE)
    private Set<Message> messages;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @NotNull
    private ThreadStatus status = ThreadStatus.OPEN;

    @ElementCollection
    private Set<String> keywords;

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

    public boolean isBlocked() {
        return getStatus() == ThreadStatus.BLOCKED;
    }
}