package com.nabagagem.connectbe.services.rating;

import com.nabagagem.connectbe.domain.rating.CreateRatingCommand;
import com.nabagagem.connectbe.domain.rating.RatingPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Rating;
import com.nabagagem.connectbe.mappers.RatingMapper;
import com.nabagagem.connectbe.repos.RatingRepository;
import com.nabagagem.connectbe.services.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingMapper mockRatingMapper;
    @Mock
    private ProfileService mockProfileService;
    @Mock
    private RatingRepository mockRatingRepository;

    private RatingService ratingServiceUnderTest;

    @BeforeEach
    void setUp() {
        ratingServiceUnderTest = new RatingService(mockRatingMapper, mockProfileService, mockRatingRepository);
    }

    @Test
    void testCreate() {
        // Setup
        RatingPayload ratingPayload = new RatingPayload(UUID.fromString("57320217-9a0e-4a94-9a8b-9fa8f772eb8d"), 0, "description");
        final CreateRatingCommand createRatingCommand = new CreateRatingCommand(
                ratingPayload,
                UUID.fromString("4f0fce12-3d6c-4377-aeda-cfe76a9435df"));
        ConnectProfile targetProfile = ConnectProfile.builder()
                .id(UUID.fromString("4f0fce12-3d6c-4377-aeda-cfe76a9435df"))
                .build();

        // Configure RatingMapper.toEntity(...).
        final Rating rating = Rating.builder()
                .targetProfile(targetProfile)
                .sourceProfile(targetProfile)
                .build();
        when(mockRatingMapper.toEntity(ratingPayload)).thenReturn(rating);

        when(mockProfileService.findOrFail(UUID.fromString("57320217-9a0e-4a94-9a8b-9fa8f772eb8d")))
                .thenReturn(targetProfile);

        // Configure RatingRepository.save(...).

        when(mockRatingRepository.save(rating)).thenReturn(rating);

        // Run the test
        final Rating result = ratingServiceUnderTest.create(createRatingCommand);

        // Verify the results
        assertThat(result).isEqualTo(rating);
    }

    @Test
    void testDelete() {
        // Setup
        // Run the test
        ratingServiceUnderTest.delete(UUID.fromString("d1df02f5-ba1f-4199-9ce4-6133771a3ccb"));

        // Verify the results
        verify(mockRatingRepository).deleteById(UUID.fromString("d1df02f5-ba1f-4199-9ce4-6133771a3ccb"));
    }
}
