package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.resources.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SlugService {
    private final ProfileRepo profileRepo;

    @Cacheable("slug")
    public UUID getProfileIdFrom(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return profileRepo.findIdFromSlug(id)
                    .orElseThrow(ProfileNotFoundException::new);
        }
    }
}
