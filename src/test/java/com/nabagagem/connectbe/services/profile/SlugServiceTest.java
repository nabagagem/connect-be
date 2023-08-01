package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.ProfileNotFoundException;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlugServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;

    private SlugService slugServiceUnderTest;

    @BeforeEach
    void setUp() {
        slugServiceUnderTest = new SlugService(mockProfileRepo);
    }

    @Test
    void testGetProfileIdFrom() {
        // Setup
        // Run the test
        final UUID result = slugServiceUnderTest.getProfileIdFrom("0db2459e-de59-41f4-9eaa-eef11582acc0");

        // Verify the results
        assertThat(result).isEqualTo(UUID.fromString("0db2459e-de59-41f4-9eaa-eef11582acc0"));
    }

    @Test
    void testGetProfileIdFrom_ProfileRepoReturnsAbsent() {
        // Setup
        String slug = "slug";
        when(mockProfileRepo.findIdFromSlug(slug)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
                () -> slugServiceUnderTest.getProfileIdFrom(slug))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    void testDoesNotExists() {
        // Setup
        // Configure ProfileRepo.findIdFromSlug(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("398ca686-e606-4bb1-879e-a079e69d29c4"));
        when(mockProfileRepo.findIdFromSlug("slug")).thenReturn(uuid);

        // Run the test
        final boolean result = slugServiceUnderTest.doesNotExists("slug");

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testDoesNotExists_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findIdFromSlug("slug")).thenReturn(Optional.empty());

        // Run the test
        final boolean result = slugServiceUnderTest.doesNotExists("slug");

        // Verify the results
        assertThat(result).isTrue();
    }

    @Test
    void testGenerateSlug() {
        // Setup
        // Configure ProfileRepo.findIdFromSlug(...).
        when(mockProfileRepo.findIdFromSlug("public-name")).thenReturn(Optional.empty());

        // Run the test
        final String result = slugServiceUnderTest.generateSlug("public name");

        // Verify the results
        assertThat(result).isEqualTo("public-name");
    }
}
