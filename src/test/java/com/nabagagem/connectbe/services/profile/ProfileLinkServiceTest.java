package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileLinksCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.ProfileLink;
import com.nabagagem.connectbe.repos.ProfileLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileLinkServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfileLinkRepository mockProfileLinkRepository;

    private ProfileLinkService profileLinkServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileLinkServiceUnderTest = new ProfileLinkService(mockProfileService, mockProfileLinkRepository);
    }

    @ParameterizedTest
    @EnumSource(LinkType.class)
    void testUpdate(LinkType linkType) {
        // Setup
        UUID profileId = UUID.fromString("1e694c2a-3a5a-49a4-bd64-179580a88ce8");
        final ProfileLinksCommand profileLinksCommand = new ProfileLinksCommand(
                profileId,
                Map.ofEntries(Map.entry(linkType, "value")));

        // Configure ProfileService.findOrCreate(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .profileLinks(Set.of(ProfileLink.builder()
                        .linkType(linkType)
                        .linkURL("linkURL")
                        .build()))
                .build();
        when(mockProfileService.findOrCreate(profileId))
                .thenReturn(profile);

        // Run the test
        profileLinkServiceUnderTest.update(profileLinksCommand);

        // Verify the results
        verify(mockProfileLinkRepository).deleteByProfile(profile);

        verify(mockProfileService).save(ConnectProfile.builder()
                .profileLinks(Set.of(ProfileLink.builder()
                        .linkType(linkType)
                        .linkURL("linkURL")
                        .build()))
                .build());
    }
}
