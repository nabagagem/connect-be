package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "profile_report", indexes = {
        @Index(name = "idx_profilereport_reporttype", columnList = "reportType"),
        @Index(name = "idx_profilereport", columnList = "target_profile_id"),
        @Index(name = "idx_profilereport", columnList = "target_job_id"),
        @Index(name = "idx_profilereport", columnList = "reportStatus")
})
@EntityListeners(AuditingEntityListener.class)
public class ProfileReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "target_profile_id", nullable = false)
    private ConnectProfile targetProfile;

    @ManyToOne
    @JoinColumn(name = "target_job_id")
    private Job targetJob;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "reporter_id", nullable = false)
    private ConnectProfile reporter;

    @Size(min = 20, max = 1000)
    @Column(length = 1000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReportStatus reportStatus = ReportStatus.PENDING;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReportAction reportAction = ReportAction.NONE;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}