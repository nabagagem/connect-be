package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "gig_location")
public class GigLocation {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    @Column(nullable = false)
    private String cityName;

    @NotEmpty
    @Column(nullable = false)
    private String stateName;

    @NotEmpty
    @Column(nullable = false)
    private String countryName;

    @ManyToOne
    @JoinColumn(name = "gig_id")
    private Gig gig;

    public Gig getGig() {
        return gig;
    }

    public void setGig(Gig gig) {
        this.gig = gig;
    }
}