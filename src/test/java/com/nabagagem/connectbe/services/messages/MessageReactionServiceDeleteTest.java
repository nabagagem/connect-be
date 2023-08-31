package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.exceptions.NotFound;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Reaction;
import com.nabagagem.connectbe.repos.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageReactionServiceDeleteTest {

    @Mock
    private ReactionRepository mockReactionRepository;

    private MessageReactionService messageReactionServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        messageReactionServiceUnderTest = new MessageReactionService(mockReactionRepository);
    }

    @Test
    void testDelete() {
        // Setup
        // Configure ReactionRepository.findById(...).
        final Optional<Reaction> reaction = Optional.of(Reaction.builder()
                .message(Message.builder()
                        .id(UUID.fromString("919208c0-03ff-4b84-afc3-7ca04f5f4da1"))
                        .build())
                .reaction("reaction")
                .build());
        when(mockReactionRepository.findById(UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838")))
                .thenReturn(reaction);

        // Run the test
        final Reaction result = messageReactionServiceUnderTest.delete(
                UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838"));

        // Verify the results
        verify(mockReactionRepository).delete(any(Reaction.class));
    }

    @Test
    void testDelete_ReactionRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockReactionRepository.findById(UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageReactionServiceUnderTest.delete(
                UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838"))).isInstanceOf(NotFound.class);
    }

    @Test
    void testDelete_ReactionRepositoryDeleteThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure ReactionRepository.findById(...).
        final Optional<Reaction> reaction = Optional.of(Reaction.builder()
                .message(Message.builder()
                        .id(UUID.fromString("919208c0-03ff-4b84-afc3-7ca04f5f4da1"))
                        .build())
                .reaction("reaction")
                .build());
        when(mockReactionRepository.findById(UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838")))
                .thenReturn(reaction);

        doThrow(OptimisticLockingFailureException.class).when(mockReactionRepository).delete(any(Reaction.class));

        // Run the test
        assertThatThrownBy(() -> messageReactionServiceUnderTest.delete(
                UUID.fromString("c23d871e-e944-4ae6-bd5d-c797cbfde838")))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
