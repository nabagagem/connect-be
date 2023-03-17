package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.MediaType;

import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_media_gig_id", columnList = "gig_id")
})
@EqualsAndHashCode(of = "id")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @ManyToOne
    @RestResource
    @JoinColumn(name = "gig_id")
    private Gig gig;
    @NotEmpty
    @Column(nullable = false)
    private String originalName;
    @NotNull
    @Column(nullable = false)
    private MediaType mediaType;
    @NotEmpty
    @Column(nullable = false)
    private byte[] fileContent;
    private String description;
    @Embedded
    private Audit audit;
}