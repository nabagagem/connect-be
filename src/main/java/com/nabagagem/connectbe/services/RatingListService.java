package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.ProfileRatingPayload;
import com.nabagagem.connectbe.mappers.RatingMapper;
import com.nabagagem.connectbe.repos.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingListService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public Page<ProfileRatingPayload> findRatingsFor(UUID profileId, Pageable pageable) {
        return ratingRepository.findRatingsForProfile(profileId, pageable)
                .map(ratingMapper::toProfileDto);
    }

    public Double getAverageFor(UUID profileId) {
        return ratingRepository.findAverageFor(profileId);
    }
}
