package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "report_pic", indexes = {
        @Index(name = "idx_reportpic_media_id", columnList = "media_id"),
        @Index(name = "idx_reportpic", columnList = "profile_report_id")
})
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