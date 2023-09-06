package com.nabagagem.connectbe.services.rating;

import com.nabagagem.connectbe.domain.rating.ProfileRatingPayload;
import com.nabagagem.connectbe.mappers.RatingMapper;
import com.nabagagem.connectbe.repos.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingListService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public Page<ProfileRatingPayload> findRatingsFor(UUID profileId, Pageable pageable) {
        return Page.empty();
    }

    public Double getAverageFor(UUID profileId) {
        return BigDecimal.ZERO.doubleValue();
    }

    public Optional<ProfileRatingPayload> findRatingsFromTo(UUID loggedUser, UUID targetUserId) {
        return Optional.empty();
    }
}
