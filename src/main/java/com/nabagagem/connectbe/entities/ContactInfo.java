package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInfo {
    private String email;
    private String countryCode;

    private String dialCode;

    private String phoneNumber;
}