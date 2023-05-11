package com.nabagagem.connectbe.entities;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "event", indexes = {
        @Index(name = "idx_event_eventmode", columnList = "eventMode"),
        @Index(name = "idx_event_eventtype", columnList = "eventType"),
        @Index(name = "idx_event_eventdate", columnList = "eventDate"),
        @Index(name = "idx_event_event_picture_id", columnList = "event_picture_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotEmpty
    @Size(min = 10, max = 200)
    @Column(nullable = false)
    private String title;

    @NotNull
    @Future
    private LocalDate eventDate;

    @Size(max = 200)
    private String address;

    @NotNull
    private MoneyAmount price;

    @URL
    private String externalLink;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_picture_id")
    private Media eventPicture;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventMode eventMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();
}