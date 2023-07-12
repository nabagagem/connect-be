package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.Bid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BidAuthService implements LoggedUserIdTrait {
    private final BidService bidService;

    public boolean isAllowedToRead(Bid bid) {
        return loggedUser()
                .filter(userId -> bid.getOwner().getId().equals(userId) ||
                        bid.getTargetJob().getOwner().getId().equals(userId))
                .isPresent();
    }

    public void failIfUnableToDelete(UUID bidId) {
        loggedUser()
                .filter(userId -> bidService.findById(bidId)
                        .map(bid -> !bid.getOwner().getId().equals(userId))
                        .orElse(true))
                .ifPresent(__ -> {
                    throw new AccessDeniedException("Access denied");
                });
    }
}
