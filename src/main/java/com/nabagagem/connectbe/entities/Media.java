package com.nabagagem.connectbe.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_media_gig_id", columnList = "gig_id"),
        @Index(name = "idx_media_account_id", columnList = "account_id")
})
@EqualsAndHashCode(of = "id")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @ManyToOne

    @JoinColumn(name = "gig_id")
    private Gig gig;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @NotEmpty
    @Column(nullable = false)
    private String originalName;
    @NotNull
    @Column(nullable = false)
    private MediaType mediaType;

    @NotEmpty
    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileContent;

    private String description;
    @Embedded
    private Audit audit;
}