package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.profile.AdminProfileCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileAdminServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfileRepo mockProfileRepo;

    private ProfileAdminService profileAdminServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileAdminServiceUnderTest = new ProfileAdminService(mockProfileService, mockProfileRepo);
    }

    @Test
    void testPatch() {
        // Setup
        final AdminProfileCommand adminProfileCommand = new AdminProfileCommand(ProfileType.USER);

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .profileType(ProfileType.USER)
                .build());
        when(mockProfileRepo.findById(UUID.fromString("11bc3a8e-eafe-4783-9fc0-b06613314da7")))
                .thenReturn(connectProfile);

        // Configure ProfileService.save(...).
        final ConnectProfile connectProfile1 = ConnectProfile.builder()
                .profileType(ProfileType.USER)
                .build();
        when(mockProfileService.save(ConnectProfile.builder()
                .profileType(ProfileType.USER)
                .build())).thenReturn(connectProfile1);

        // Run the test
        profileAdminServiceUnderTest.patch(UUID.fromString("11bc3a8e-eafe-4783-9fc0-b06613314da7"),
                adminProfileCommand);

        // Verify the results
    }

    @Test
    void testPatch_ProfileRepoReturnsAbsent() {
        // Setup
        final AdminProfileCommand adminProfileCommand = new AdminProfileCommand(ProfileType.USER);
        when(mockProfileRepo.findById(UUID.fromString("11bc3a8e-eafe-4783-9fc0-b06613314da7")))
                .thenReturn(Optional.empty());

        // Run the test
        profileAdminServiceUnderTest.patch(UUID.fromString("11bc3a8e-eafe-4783-9fc0-b06613314da7"),
                adminProfileCommand);

        // Verify the results
    }
}
