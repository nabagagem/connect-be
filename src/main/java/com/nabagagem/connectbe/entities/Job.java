package com.nabagagem.connectbe.entities;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobMode;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "job", indexes = {
        @Index(name = "idx_job_jobcategory", columnList = "jobCategory"),
        @Index(name = "idx_job_jobsize", columnList = "jobSize"),
        @Index(name = "idx_job_jobfrequency", columnList = "jobFrequency"),
        @Index(name = "idx_job_jobmode", columnList = "jobMode"),
        @Index(name = "idx_job_requiredavailability", columnList = "requiredAvailability"),
        @Index(name = "idx_job_owner_id", columnList = "owner_id"),
        @Index(name = "idx_job_jobstatus", columnList = "jobStatus"),
        @Index(columnList = "startAt"),
        @Index(columnList = "finishAt")
})
@EntityListeners(AuditingEntityListener.class)
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false, length = 100)
    private String title;
    @Valid
    @Embedded
    MoneyAmount budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory jobCategory;

    @NotBlank
    @Size(min = 10, max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private JobSize jobSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private JobFrequency jobFrequency;

    @Column(length = 1000)
    @Size(max = 1000)
    private String background;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobMode jobMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobRequiredAvailability requiredAvailability;

    @Size(max = 200)
    @Column(length = 200)
    private String address;

    @Size(max = 200)
    @Column(length = 200)
    private String addressReference;

    @Size(max = 10)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Skill> requiredSkills;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "owner_id", nullable = false)
    private ConnectProfile owner;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus jobStatus;

    @ElementCollection
    private Set<@NotBlank String> tags;

    @Embedded
    private DateInterval requiredDates;

    @ElementCollection
    private Set<@NotBlank String> keywords;

    @OneToMany(mappedBy = "job")
    private Set<JobMedia> jobMedia;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}