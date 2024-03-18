package com.nabagagem.connectbe.services.bid;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.entities.Bid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BidAuthService  {
    private final BidService bidService;
    private final LoginHelper loginHelper;

    public boolean isAllowedToRead(Bid bid) {
        return loginHelper.loggedUser()
                .filter(userId -> bid.getOwner().getId().equals(userId) ||
                        bid.getTargetJob().getOwner().getId().equals(userId))
                .isPresent();
    }

    public void failIfUnableToDelete(UUID bidId) {
        loginHelper.loggedUser()
                .filter(userId -> bidService.findById(bidId)
                        .map(bid -> !bid.getOwner().getId().equals(userId))
                        .orElse(true))
                .ifPresent(__ -> {
                    throw new AccessDeniedException("Access denied");
                });
    }
}
