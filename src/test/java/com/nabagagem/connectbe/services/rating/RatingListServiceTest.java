package com.nabagagem.connectbe.services.rating;

import com.nabagagem.connectbe.domain.rating.ProfileRatingPayload;
import com.nabagagem.connectbe.entities.Rating;
import com.nabagagem.connectbe.mappers.RatingMapper;
import com.nabagagem.connectbe.repos.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingListServiceTest {

    @Mock
    private RatingRepository mockRatingRepository;
    @Mock
    private RatingMapper mockRatingMapper;

    private RatingListService ratingListServiceUnderTest;

    @BeforeEach
    void setUp() {
        ratingListServiceUnderTest = new RatingListService(mockRatingRepository, mockRatingMapper);
    }

    @Test
    void testFindRatingsFor() throws Exception {
        // Setup
        // Configure RatingRepository.findRatingsForProfile(...).
        Rating rating = Rating.builder()
                .id(UUID.randomUUID())
                .build();
        final Page<Rating> ratings = new PageImpl<>(List.of(rating));
        when(mockRatingRepository.findRatingsForProfile(eq(UUID.fromString("dc098f99-7465-4088-b50a-9ca7ebd89c18")),
                any(Pageable.class))).thenReturn(ratings);

        // Configure RatingMapper.toProfileDto(...).
        final ProfileRatingPayload profileRatingPayload = new ProfileRatingPayload("sourceProfilePublicName",
                new URL("https://example.com/"), UUID.fromString("9aac74aa-a3a4-424c-9421-0f1a3b1a8500"), 0,
                "description", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        when(mockRatingMapper.toProfileDto(rating)).thenReturn(profileRatingPayload);

        // Run the test
        final Page<ProfileRatingPayload> result = ratingListServiceUnderTest.findRatingsFor(
                UUID.fromString("dc098f99-7465-4088-b50a-9ca7ebd89c18"), PageRequest.of(0, 1));

        // Verify the results
        assertThat(result).containsExactly(profileRatingPayload);
    }

    @Test
    void testFindRatingsFor_RatingRepositoryReturnsNoItems() throws Exception {
        // Setup
        when(mockRatingRepository.findRatingsForProfile(eq(UUID.fromString("dc098f99-7465-4088-b50a-9ca7ebd89c18")),
                any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Configure RatingMapper.toProfileDto(...).
        final ProfileRatingPayload profileRatingPayload = new ProfileRatingPayload("sourceProfilePublicName",
                new URL("https://example.com/"), UUID.fromString("9aac74aa-a3a4-424c-9421-0f1a3b1a8500"), 0,
                "description", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));

        // Run the test
        final Page<ProfileRatingPayload> result = ratingListServiceUnderTest.findRatingsFor(
                UUID.fromString("dc098f99-7465-4088-b50a-9ca7ebd89c18"), PageRequest.of(0, 1));

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAverageFor() {
        // Setup
        when(mockRatingRepository.findAverageFor(UUID.fromString("46a711b2-9ffd-44d9-b8b1-7e0847c2ee80")))
                .thenReturn(0.0);

        // Run the test
        final Double result = ratingListServiceUnderTest.getAverageFor(
                UUID.fromString("46a711b2-9ffd-44d9-b8b1-7e0847c2ee80"));

        // Verify the results
        assertThat(result).isEqualTo(0.0, within(0.0001));
    }

    @Test
    void testFindRatingsFromTo() throws Exception {
        // Setup
        Rating rating = Rating.builder()
                .id(UUID.randomUUID())
                .build();
        when(mockRatingRepository.findFromTo(UUID.fromString("7ffbf7ce-d8e4-4833-99bd-37ab1c5842b8"),
                UUID.fromString("7e28c108-b360-4549-8dd4-25f146e47eb5")))
                .thenReturn(Optional.of(rating));

        // Configure RatingMapper.toProfileDto(...).
        final ProfileRatingPayload profileRatingPayload = new ProfileRatingPayload("sourceProfilePublicName",
                new URL("https://example.com/"), UUID.fromString("93dc7aec-c908-412d-b4af-e4902a49599d"), 0,
                "description", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        when(mockRatingMapper.toProfileDto(rating)).thenReturn(profileRatingPayload);

        // Run the test
        final Optional<ProfileRatingPayload> result = ratingListServiceUnderTest.findRatingsFromTo(
                UUID.fromString("7ffbf7ce-d8e4-4833-99bd-37ab1c5842b8"),
                UUID.fromString("7e28c108-b360-4549-8dd4-25f146e47eb5"));

        // Verify the results
        assertThat(result).contains(profileRatingPayload);
    }

    @Test
    void testFindRatingsFromTo_RatingRepositoryReturnsAbsent() {
        // Setup
        when(mockRatingRepository.findFromTo(UUID.fromString("7ffbf7ce-d8e4-4833-99bd-37ab1c5842b8"),
                UUID.fromString("7e28c108-b360-4549-8dd4-25f146e47eb5"))).thenReturn(Optional.empty());

        // Run the test
        final Optional<ProfileRatingPayload> result = ratingListServiceUnderTest.findRatingsFromTo(
                UUID.fromString("7ffbf7ce-d8e4-4833-99bd-37ab1c5842b8"),
                UUID.fromString("7e28c108-b360-4549-8dd4-25f146e47eb5"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
