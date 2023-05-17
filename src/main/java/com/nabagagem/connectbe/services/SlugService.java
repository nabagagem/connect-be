package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SlugService {
    private final ProfileRepo profileRepo;

    public UUID getProfileIdFrom(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return profileRepo.findIdFromSlug(id)
                    .orElseThrow(ProfileNotFoundException::new);
        }
    }

    public ConnectProfile findByRefOrFail(String owner) {
        return profileRepo.findById(getProfileIdFrom(owner))
                .orElseThrow(ProfileNotFoundException::new);
    }
}
