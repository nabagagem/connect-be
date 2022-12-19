package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "gig",
        indexes = {
                @Index(columnList = "account_id"),
                @Index(columnList = "created_by"),
                @Index(columnList = "modified_by")
        })
@EntityListeners(AuditingEntityListener.class)
public class Gig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    @Column(nullable = false)
    private String title;

    @NotEmpty
    @Column(nullable = false)
    private String summary;

    @ElementCollection
    @Column(name = "tag")
    @JoinColumn(name = "gig_id")
    private Set<String> tags;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}