package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.ReactionPayload;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Reaction;
import com.nabagagem.connectbe.repos.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageReactionServiceTest {

    @Mock
    private ReactionRepository mockReactionRepository;

    private MessageReactionService messageReactionServiceUnderTest;

    @BeforeEach
    void setUp() {
        messageReactionServiceUnderTest = new MessageReactionService(mockReactionRepository);
    }

    @Test
    void testCreate() {
        // Setup
        final ReactionPayload reactionPayload = new ReactionPayload("reaction");

        // Configure ReactionRepository.save(...).
        final Reaction reaction = Reaction.builder()
                .message(Message.builder()
                        .id(UUID.fromString("ca654bd4-0b45-4d86-b57d-6ea9e80fc19e"))
                        .build())
                .reaction("reaction")
                .build();
        when(mockReactionRepository.save(any(Reaction.class))).thenReturn(reaction);

        // Run the test
        final Reaction result = messageReactionServiceUnderTest.create(
                UUID.fromString("ca654bd4-0b45-4d86-b57d-6ea9e80fc19e"), reactionPayload);

        // Verify the results
    }

    @Test
    void testCreate_ReactionRepositoryThrowsOptimisticLockingFailureException() {
        // Setup
        final ReactionPayload reactionPayload = new ReactionPayload("reaction");
        when(mockReactionRepository.save(any(Reaction.class))).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(
                () -> messageReactionServiceUnderTest.create(UUID.fromString("ca654bd4-0b45-4d86-b57d-6ea9e80fc19e"),
                        reactionPayload)).isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testDelete() {
        // Setup
        // Run the test
        messageReactionServiceUnderTest.delete(UUID.fromString("8bcaff48-1c4d-4040-bb15-035da8f41d2b"));

        // Verify the results
        verify(mockReactionRepository).deleteById(UUID.fromString("8bcaff48-1c4d-4040-bb15-035da8f41d2b"));
    }
}
