package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address", indexes = {
        @Index(columnList = "country"),
        @Index(columnList = "account_id")
})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @NotNull
    @Column(nullable = false)
    private String zipCode;

    @NotNull
    @Column(nullable = false)
    private String country;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}