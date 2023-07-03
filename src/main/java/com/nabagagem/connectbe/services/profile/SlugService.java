package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
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

    public boolean doesNotExists(String slug) {
        return profileRepo.findIdFromSlug(slug)
                .isEmpty();
    }
}
