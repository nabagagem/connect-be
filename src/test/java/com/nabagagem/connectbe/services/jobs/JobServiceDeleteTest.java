package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobServiceDeleteTest {

    @Mock
    private JobRepo mockJobRepo;
    @Mock
    private SkillService mockSkillService;
    @Mock
    private JobMapper mockJobMapper;
    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private JobIndexService mockJobIndexService;
    @Mock
    private JobFileService mockJobFileService;

    private JobService jobServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobServiceUnderTest = new JobService(mockJobRepo, mockSkillService, mockJobMapper, mockProfileRepo,
                mockJobIndexService, mockJobFileService);
    }

    @Test
    void testDelete() {
        // Setup
        // Run the test
        jobServiceUnderTest.delete(UUID.fromString("68a5cd2a-2b2f-4eea-a154-1990dae37c44"));

        // Verify the results
        verify(mockJobFileService).deleteAllForJob(UUID.fromString("68a5cd2a-2b2f-4eea-a154-1990dae37c44"));
        verify(mockJobRepo).deleteById(UUID.fromString("68a5cd2a-2b2f-4eea-a154-1990dae37c44"));
    }
}
