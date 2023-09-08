package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobMode;
import com.nabagagem.connectbe.domain.job.JobPatchPayload;
import com.nabagagem.connectbe.domain.job.JobPayload;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceWriteTest {

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

    private JobService jobServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobServiceUnderTest = new JobService(mockJobRepo, mockSkillService, mockJobMapper, mockProfileRepo,
                mockJobIndexService, null);
    }

    @Test
    void testCreate() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("144fa7a4-e6e9-4db5-a096-218ff677979d"),
                UUID.fromString("655928c7-f9a9-4884-8b48-0f361cc3100d"), "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("skillName"),
                JobStatus.PUBLISHED, Set.of("value"));
        final Job expectedResult = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();

        // Configure JobMapper.map(...).

        when(mockJobMapper.map(jobPayload)).thenReturn(expectedResult);

        when(mockSkillService.findOrCreate("skillName")).thenReturn(Skill.builder().name("skillName").build());

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder().build());
        when(mockProfileRepo.findById(UUID.fromString("84531a6b-7071-438e-9afe-ff9a0636c700")))
                .thenReturn(connectProfile);

        when(mockJobIndexService.extractFrom(expectedResult)).thenReturn(Set.of("value"));

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(expectedResult)).thenReturn(expectedResult);

        // Run the test
        final Job result = jobServiceUnderTest.create(jobPayload,
                UUID.fromString("84531a6b-7071-438e-9afe-ff9a0636c700"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCreate_ProfileRepoReturnsAbsent() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("144fa7a4-e6e9-4db5-a096-218ff677979d"),
                UUID.fromString("655928c7-f9a9-4884-8b48-0f361cc3100d"), "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("value"),
                JobStatus.PUBLISHED, Set.of("value"));

        // Configure JobMapper.map(...).
        final Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();

        when(mockJobMapper.map(jobPayload)).thenReturn(job);

        when(mockSkillService.findOrCreate("value")).thenReturn(Skill.builder().name("value").build());
        when(mockProfileRepo.findById(UUID.fromString("84531a6b-7071-438e-9afe-ff9a0636c700")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> jobServiceUnderTest.create(jobPayload,
                UUID.fromString("84531a6b-7071-438e-9afe-ff9a0636c700"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testSave() {
        // Setup
        final Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();

        when(mockJobIndexService.extractFrom(job)).thenReturn(Set.of("value"));

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenReturn(job);

        // Run the test
        final Job result = jobServiceUnderTest.save(job);

        // Verify the results
        assertThat(result).isEqualTo(job);
    }

    @Test
    void testSave_JobIndexServiceReturnsNoItems() {
        // Setup
        final Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();

        when(mockJobIndexService.extractFrom(job)).thenReturn(Collections.emptySet());

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(job)).thenReturn(job);

        // Run the test
        final Job result = jobServiceUnderTest.save(job);

        // Verify the results
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(job);
    }

    @Test
    void testGetJob() {
        // Setup
        // Configure JobRepo.findById(...).
        final Optional<Job> job = Optional.of(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build());
        when(mockJobRepo.findById(UUID.fromString("4ec59eef-fb1b-4c3b-be92-27808c44bcd6"))).thenReturn(job);

        // Configure JobMapper.toDto(...).
        final MoneyAmount moneyAmount1 = new MoneyAmount();
        moneyAmount1.setAmount(new BigDecimal("0.00"));
        moneyAmount1.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval1 = new DateInterval();
        dateInterval1.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval1.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("69b15ac3-d65a-4074-9b89-bd231b4c0e54"),
                UUID.fromString("8bdb37c6-7fe3-4ea2-960b-ec64469d3703"), "title", moneyAmount1, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval1, "address", "addressReference", Set.of("value"),
                JobStatus.PUBLISHED, Set.of("value"));
        when(mockJobMapper.toDto(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenReturn(jobPayload);

        // Run the test
        final Optional<JobPayload> result = jobServiceUnderTest.getJob("4ec59eef-fb1b-4c3b-be92-27808c44bcd6");

        // Verify the results
        assertThat(result).containsSame(jobPayload);
    }

    @Test
    void testGetJob_JobRepoReturnsAbsent() {
        // Setup
        when(mockJobRepo.findById(UUID.fromString("4ec59eef-fb1b-4c3b-be92-27808c44bcd6")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<JobPayload> result = jobServiceUnderTest.getJob("4ec59eef-fb1b-4c3b-be92-27808c44bcd6");

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdate() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("e2aa88e2-8374-4656-a0cc-3edf2c6e801d"),
                UUID.fromString("e9752fae-b07e-4b6e-8a4d-57474d7dd645"), "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("skillName"),
                JobStatus.PUBLISHED, Set.of("value"));

        // Configure JobRepo.findById(...).
        Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();
        final Optional<Job> jobOptional = Optional.of(job);
        when(mockJobRepo.findById(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3"))).thenReturn(jobOptional);

        when(mockSkillService.findOrCreate("skillName")).thenReturn(Skill.builder().build());
        when(mockJobIndexService.extractFrom(job)).thenReturn(Set.of("value"));

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(job)).thenReturn(job);

        // Run the test
        jobServiceUnderTest.update(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3"), jobPayload);

        // Verify the results
        // Confirm JobMapper.map(...).
        verify(mockJobMapper).map(job, jobPayload);
    }

    @Test
    void testUpdate_JobRepoFindByIdReturnsAbsent() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("e2aa88e2-8374-4656-a0cc-3edf2c6e801d"),
                UUID.fromString("e9752fae-b07e-4b6e-8a4d-57474d7dd645"), "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("value"),
                JobStatus.PUBLISHED, Set.of("value"));
        when(mockJobRepo.findById(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> jobServiceUnderTest.update(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3"),
                jobPayload)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testUpdate_JobIndexServiceReturnsNoItems() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(UUID.fromString("e2aa88e2-8374-4656-a0cc-3edf2c6e801d"),
                UUID.fromString("e9752fae-b07e-4b6e-8a4d-57474d7dd645"), "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("skillName"),
                JobStatus.PUBLISHED, Set.of("job"));

        // Configure JobRepo.findById(...).
        Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("job"))
                .build();
        final Optional<Job> optionalJob = Optional.of(job);
        when(mockJobRepo.findById(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3"))).thenReturn(optionalJob);

        when(mockSkillService.findOrCreate("skillName")).thenReturn(Skill.builder().build());
        when(mockJobIndexService.extractFrom(job)).thenReturn(Collections.emptySet());

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(job)).thenReturn(job);

        // Run the test
        jobServiceUnderTest.update(UUID.fromString("80ec7f04-0346-4afd-a1c5-1a5e386670e3"), jobPayload);

        // Verify the results
        // Confirm JobMapper.map(...).

        verify(mockJobMapper).map(job, jobPayload);
    }

    @Test
    void testPatch() {
        // Setup
        final JobPatchPayload jobPatchPayload = new JobPatchPayload(JobStatus.PUBLISHED);

        // Configure JobRepo.findById(...).
        Job job = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("job"))
                .build();
        final Optional<Job> optionalJob = Optional.of(job);
        when(mockJobRepo.findById(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"))).thenReturn(optionalJob);

        when(mockJobIndexService.extractFrom(job)).thenReturn(Set.of("job"));

        // Configure JobRepo.save(...).
        when(mockJobRepo.save(job)).thenReturn(job);

        // Run the test
        jobServiceUnderTest.patch(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"), jobPatchPayload);
    }

    @Test
    void testPatch_JobRepoFindByIdReturnsAbsent() {
        // Setup
        final JobPatchPayload jobPatchPayload = new JobPatchPayload(JobStatus.PUBLISHED);
        when(mockJobRepo.findById(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> jobServiceUnderTest.patch(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"),
                jobPatchPayload)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testPatch_JobIndexServiceReturnsNoItems() {
        // Setup
        final JobPatchPayload jobPatchPayload = new JobPatchPayload(JobStatus.PUBLISHED);

        // Configure JobRepo.findById(...).
        final Optional<Job> job = Optional.of(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build());
        when(mockJobRepo.findById(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"))).thenReturn(job);

        when(mockJobIndexService.extractFrom(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenReturn(Collections.emptySet());

        // Configure JobRepo.save(...).
        final Job job1 = Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build();
        when(mockJobRepo.save(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenReturn(job1);

        // Run the test
        jobServiceUnderTest.patch(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"), jobPatchPayload);

        // Verify the results
    }

    @Test
    void testPatch_JobRepoSaveThrowsOptimisticLockingFailureException() {
        // Setup
        final JobPatchPayload jobPatchPayload = new JobPatchPayload(JobStatus.PUBLISHED);

        // Configure JobRepo.findById(...).
        final Optional<Job> job = Optional.of(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build());
        when(mockJobRepo.findById(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"))).thenReturn(job);

        when(mockJobIndexService.extractFrom(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));
        when(mockJobRepo.save(Job.builder()
                .requiredSkills(Set.of(Skill.builder().build()))
                .owner(ConnectProfile.builder().build())
                .jobStatus(JobStatus.PUBLISHED)
                .keywords(Set.of("value"))
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> jobServiceUnderTest.patch(UUID.fromString("de90b5ce-7a56-41d8-b2fa-107232ef7a36"),
                jobPatchPayload)).isInstanceOf(OptimisticLockingFailureException.class);
    }
}
