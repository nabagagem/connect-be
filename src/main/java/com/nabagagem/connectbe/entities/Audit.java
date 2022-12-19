package com.nabagagem.connectbe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

@Data
@Embeddable
@EntityListeners(AuditingEntityListener.class)
public class Audit {
    @ManyToOne
    @CreatedBy
    @RestResource
    @JoinColumn(name = "created_by", updatable = false)
    private Account createdBy;

    @ManyToOne
    @LastModifiedBy
    @RestResource
    @JoinColumn(name = "modified_by")
    private Account modifiedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}