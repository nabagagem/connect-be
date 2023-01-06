package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Entity
@Table(name = "gig_mode", indexes = {
        @Index(name = "uk_mode_name", columnList = "name", unique = true)
})
@EqualsAndHashCode(of = "id")
public class GigMode {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    private String name;
}