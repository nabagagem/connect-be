package com.nabagagem.connectbe.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.MediaType;

import java.util.UUID;

@Getter
@Setter
@Data
@Entity
@Table(name = "media")
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @NotEmpty
    @Column(nullable = false)
    private String originalName;
    @NotNull
    @Column(nullable = false)
    private MediaType mediaType;

    @OneToOne(mappedBy = "profilePicture")
    private ConnectProfile profile;

    @NotEmpty
    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileContent;

    private String description;
    @Embedded
    private Audit audit;
}