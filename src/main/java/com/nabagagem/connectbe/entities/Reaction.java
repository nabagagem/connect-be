package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reaction", indexes = {
        @Index(name = "idx_reaction_message_id", columnList = "message_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_reaction_profile", columnNames = {"message_id", "reaction", "created_by"})
})
@EntityListeners(AuditingEntityListener.class)
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @NotBlank
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String reaction;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

}