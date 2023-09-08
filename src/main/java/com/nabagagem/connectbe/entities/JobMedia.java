package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.domain.FilePurpose;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "job_media", indexes = {
        @Index(name = "idx_jobmedia_job_id", columnList = "job_id"),
        @Index(name = "idx_jobmedia_media_id", columnList = "media_id"),
        @Index(name = "idx_jobmedia_filetype", columnList = "file_purpose")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_jobmedia_position", columnNames = {"position", "job_id", "file_purpose"})
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobMedia {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "file_purpose")
    private FilePurpose filePurpose;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}