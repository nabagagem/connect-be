package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Gdpr;
import com.nabagagem.connectbe.entities.GdprLevel;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GdprServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfileRepo mockProfileRepo;

    private GdprService gdprServiceUnderTest;

    @BeforeEach
    void setUp() {
        gdprServiceUnderTest = new GdprService(mockProfileService, mockProfileRepo);
    }

    @Test
    void testUpdate() {
        // Setup
        final Gdpr gdpr = new Gdpr();
        gdpr.setGdprLevels(Collections.singleton(GdprLevel.STRICT));

        // Configure ProfileService.findOrCreate(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .gdpr(new Gdpr())
                .build();
        when(mockProfileService.findOrCreate(UUID.fromString("d42411b0-1983-4b67-875c-494ebebeda6a")))
                .thenReturn(profile);

        // Run the test
        gdprServiceUnderTest.update(UUID.fromString("d42411b0-1983-4b67-875c-494ebebeda6a"), gdpr);

        // Verify the results
        verify(mockProfileService).save(ConnectProfile.builder()
                .gdpr(new Gdpr())
                .build());
    }

    @Test
    void testGet() {
        // Setup
        // Configure ProfileRepo.findById(...).
        Set<GdprLevel> gdprLevels = Collections.singleton(GdprLevel.ANALYTICAL);
        when(mockProfileRepo.findGdprFrom(UUID.fromString("83fd8137-315f-478d-b317-ee1792414cca")))
                .thenReturn(gdprLevels);

        // Run the test
        Set<GdprLevel> result = gdprServiceUnderTest.get(UUID.fromString("83fd8137-315f-478d-b317-ee1792414cca"));

        // Verify the results
        assertThat(result).isEqualTo(gdprLevels);
    }

    @Test
    void testGet_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findGdprFrom(UUID.fromString("83fd8137-315f-478d-b317-ee1792414cca")))
                .thenReturn(Set.of());

        // Run the test
        Set<GdprLevel> result = gdprServiceUnderTest.get(UUID.fromString("83fd8137-315f-478d-b317-ee1792414cca"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
