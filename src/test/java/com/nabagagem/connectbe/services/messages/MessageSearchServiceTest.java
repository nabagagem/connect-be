package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.MessageSearchParams;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSearchServiceTest {

    @Mock
    private KeywordService mockKeywordService;
    @Mock
    private MessageRepo mockMessageRepo;

    private MessageSearchService messageSearchServiceUnderTest;

    @BeforeEach
    void setUp() {
        messageSearchServiceUnderTest = new MessageSearchService(mockKeywordService, mockMessageRepo);
    }

    @Test
    void testGetMessagesPageFrom() {
        // Setup
        final MessageSearchParams messageSearchParams = new MessageSearchParams("expression");
        when(mockKeywordService.extractFrom("expression")).thenReturn(Set.of("search"));
        when(mockMessageRepo.findMessageIdsByThread(eq(UUID.fromString("164fc54a-8594-42ca-9395-4f3d29ded0d1")),
                eq(Set.of("search")), eq(false), any(Pageable.class))).thenReturn(new PageImpl<>(List.of("value")));
        Message message = Message.builder()
                .text("msg")
                .build();
        when(mockMessageRepo.findFullPageByIds(List.of("value"))).thenReturn(List.of(message));

        // Run the test
        final Page<Message> result = messageSearchServiceUnderTest.getMessagesPageFrom(
                UUID.fromString("164fc54a-8594-42ca-9395-4f3d29ded0d1"), PageRequest.of(0, 1), messageSearchParams);

        // Verify the results
        assertThat(result)
                .extracting(Message::getText)
                .containsExactly("msg");
    }

    @Test
    void testGetMessagesPageFrom_MessageRepoFindMessageIdsByThreadReturnsNoItems() {
        // Setup
        final MessageSearchParams messageSearchParams = new MessageSearchParams("expression");
        when(mockKeywordService.extractFrom("expression")).thenReturn(Set.of("value"));
        when(mockMessageRepo.findMessageIdsByThread(eq(UUID.fromString("164fc54a-8594-42ca-9395-4f3d29ded0d1")),
                eq(Set.of("value")), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(mockMessageRepo.findFullPageByIds(Collections.emptyList())).thenReturn(List.of());

        // Run the test
        final Page<Message> result = messageSearchServiceUnderTest.getMessagesPageFrom(
                UUID.fromString("164fc54a-8594-42ca-9395-4f3d29ded0d1"), PageRequest.of(0, 1), messageSearchParams);

        // Verify the results
        assertThat(result).isEmpty();
    }
}
