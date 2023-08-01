package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LastActivityServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;

    private LastActivityService lastActivityServiceUnderTest;

    @BeforeEach
    void setUp() {
        lastActivityServiceUnderTest = new LastActivityService(mockProfileRepo);
    }

    @Test
    void testRegister() {
        // Setup
        // Run the test
        lastActivityServiceUnderTest.register(UUID.fromString("61bf6419-0dbc-42f4-bcd4-30af3f0c6425"));

        // Verify the results
        verify(mockProfileRepo).updateLastActivityFor(eq(UUID.fromString("61bf6419-0dbc-42f4-bcd4-30af3f0c6425")),
                any());
    }
}
