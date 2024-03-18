package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.IntStream;

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

    public String generateSlug(String publicName) {
        String slug = publicName
                .replaceAll("\\s", "-")
                .toLowerCase().trim();
        return IntStream.range(0, 3)
                .mapToObj(t -> slug.concat(t == 0 ? "" : String.valueOf(t)))
                .filter(this::doesNotExists)
                .findAny().orElse(slug);
    }
}
