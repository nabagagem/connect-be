package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "report_pic")
@EntityListeners(AuditingEntityListener.class)
public class ReportPic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "profile_report_id", nullable = false)
    private ProfileReport profileReport;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}