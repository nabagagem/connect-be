package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.repos.ReactionRepository;
import com.nabagagem.connectbe.services.UnwrapLoggedUserIdTrait;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReactAuthService implements UnwrapLoggedUserIdTrait {
    private final ReactionRepository reactionRepository;

    public void failIfUnableToDelete(UUID reactionId) {
        if (!reactionRepository.existsByIdAndOwner(
                reactionId,
                getLoggedUserId().toString()
        )) {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
