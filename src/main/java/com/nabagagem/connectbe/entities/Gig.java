package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.rest.core.annotation.RestResource;

import java.math.BigDecimal;
import java.util.List;
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
                @Index(columnList = "modified_by"),
                @Index(columnList = "gig_area_id"),
                @Index(columnList = "gig_mode_id"),
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

    @ManyToOne
    @JoinColumn(name = "gig_area_id")
    private GigArea gigArea;

    private String otherGigArea;

    private Boolean remoteOnly;

    @RestResource
    @OneToMany(mappedBy = "gig")
    private List<GigLocation> gigLocations;

    @ManyToOne
    @RestResource
    @JoinColumn(name = "gig_mode_id")
    private GigMode gigMode;

    @ElementCollection
    @Column(name = "tag")
    @JoinColumn(name = "gig_id")
    private Set<String> tags;

    @Positive
    private BigDecimal referencePrice;

    private Boolean openForNegotiation;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private GigVisibility gigVisibility = GigVisibility.PRIVATE;

    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();

    @ManyToOne
    @RestResource
    @JoinColumn(name = "account_id")
    private Account account;
}