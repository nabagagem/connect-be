package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "approach_message",
        indexes = {
                @Index(columnList = "sender_id"),
                @Index(columnList = "recipient_id"),
                @Index(columnList = "approach_id")
        })
public class ApproachMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "sender_id")
    private Account sender;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "recipient_id")
    private Account recipient;

    @NotEmpty
    private String text;
}