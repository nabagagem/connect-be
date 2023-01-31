package com.nabagagem.connectbe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "approach", indexes = {
        @Index(columnList = "gig_id"),
        @Index(columnList = "approached_by_id"),
        @Index(columnList = "gig_id,approached_by_id",
                unique = true, name = "uk_gig_approached_by"),
        @Index(columnList = "created_by"),
        @Index(columnList = "modified_by")
})
public class Approach {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @NotNull
    @RestResource
    @JoinColumn(name = "gig_id")
    private Gig gig;

    @ManyToOne
    @NotNull
    @RestResource
    @JoinColumn(name = "approached_by_id")
    private Account approachedBy;

    @OneToMany(mappedBy = "approach")
    @RestResource
    private Set<ApproachMessage> messages;

    @Embedded
    @Builder.Default
    @JsonIgnore
    private Audit audit = new Audit();

}