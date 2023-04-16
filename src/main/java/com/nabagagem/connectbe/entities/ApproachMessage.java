package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "approach_message",
        indexes = {
                @Index(columnList = "approach_id"),
                @Index(columnList = "messageType"),
                @Index(columnList = "created_by"),
                @Index(columnList = "modified_by")
        })
@EqualsAndHashCode(of = "id")
public class ApproachMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    private String text;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne

    @JoinColumn(name = "approach_id")
    private Approach approach;

    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();
}