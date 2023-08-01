package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.SkillRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepo mockSkillRepo;

    private SkillService skillServiceUnderTest;

    @BeforeEach
    void setUp() {
        skillServiceUnderTest = new SkillService(mockSkillRepo);
    }

    @Test
    void testFindOrCreate() {
        // Setup
        final Skill expectedResult = Skill.builder()
                .name("skillName")
                .build();

        // Configure SkillRepo.findByName(...).
        final Optional<Skill> skill = Optional.of(Skill.builder()
                .name("skillName")
                .build());
        when(mockSkillRepo.findByName("skillName")).thenReturn(skill);

        // Run the test
        final Skill result = skillServiceUnderTest.findOrCreate("skillName");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindOrCreate_SkillRepoFindByNameReturnsAbsent() {
        // Setup
        final Skill expectedResult = Skill.builder()
                .name("skillName")
                .build();
        when(mockSkillRepo.findByName("skillName")).thenReturn(Optional.empty());

        // Configure SkillRepo.save(...).
        final Skill skill = Skill.builder()
                .name("skillName")
                .build();
        when(mockSkillRepo.save(Skill.builder()
                .name("skillName")
                .build())).thenReturn(skill);

        // Run the test
        final Skill result = skillServiceUnderTest.findOrCreate("skillName");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindOrCreate_SkillRepoSaveThrowsOptimisticLockingFailureException() {
        // Setup
        when(mockSkillRepo.findByName("skillName")).thenReturn(Optional.empty());
        when(mockSkillRepo.save(Skill.builder()
                .name("skillName")
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> skillServiceUnderTest.findOrCreate("skillName"))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
