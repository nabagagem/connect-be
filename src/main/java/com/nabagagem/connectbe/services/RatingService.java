package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.CreateRatingCommand;
import com.nabagagem.connectbe.domain.RatingPayload;
import com.nabagagem.connectbe.entities.Rating;
import com.nabagagem.connectbe.mappers.RatingMapper;
import com.nabagagem.connectbe.repos.RatingRepository;
import com.nabagagem.connectbe.services.profile.ProfileService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RatingService {
    private final RatingMapper ratingMapper;
    private final ProfileService profileService;
    private final RatingRepository ratingRepository;

    public Rating create(@Valid CreateRatingCommand createRatingCommand) {
        RatingPayload ratingPayload = createRatingCommand.ratingPayload();
        Rating rating = ratingMapper.toEntity(ratingPayload);
        rating.setTargetProfile(profileService.findOrFail(ratingPayload.targetProfileId()));
        rating.setSourceProfile(profileService.findOrFail(createRatingCommand.sourceUserId()));
        return ratingRepository.save(rating);
    }

    public void delete(UUID id) {
        ratingRepository.deleteById(id);
    }
}
