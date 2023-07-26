package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.profile.CreateMessageFileCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageFileServiceTest {

    @Mock
    private MediaService mockMediaService;
    @Mock
    private MessageRepo mockMessageRepo;
    @Mock
    private ThreadRepo mockThreadRepo;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    private MessageFileService messageFileServiceUnderTest;

    @BeforeEach
    void setUp() {
        messageFileServiceUnderTest = new MessageFileService(mockMediaService, mockMessageRepo, mockThreadRepo);
    }

    @Test
    void testCreate() {
        // Setup
        UUID threadId = UUID.fromString("451dde83-f1f7-47bf-80ca-daec7cf3b600");
        final CreateMessageFileCommand createMessageFileCommand = new CreateMessageFileCommand(
                new MockMultipartFile("name", "content".getBytes()), "text",
                threadId);
        final ResourceRef expectedResult = new ResourceRef("5c804e7e-cbd4-4f8e-8e82-58aca5144039");
        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(Media.builder().build());
        when(mockThreadRepo.findById(threadId))
                .thenReturn(Optional.of(Thread.builder().build()));

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .id(UUID.fromString("5c804e7e-cbd4-4f8e-8e82-58aca5144039"))
                .thread(Thread.builder().build())
                .text("text")
                .media(Media.builder().build())
                .build();
        when(mockMessageRepo.save(any())).thenReturn(message);

        // Run the test
        final ResourceRef result = messageFileServiceUnderTest.create(createMessageFileCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMessageRepo).save(messageArgumentCaptor.capture());
        assertThat(messageArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(message);
    }

    @Test
    void testGetPicFor() {
        // Setup
        final Optional<Media> expectedResult = Optional.of(Media.builder().build());
        when(mockMessageRepo.findMediaFor(UUID.fromString("ccc1fbb9-960c-4d88-9251-6e6799da1301")))
                .thenReturn(Optional.of(Media.builder().build()));

        // Run the test
        final Optional<Media> result = messageFileServiceUnderTest.getPicFor(
                UUID.fromString("ccc1fbb9-960c-4d88-9251-6e6799da1301"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetPicFor_MessageRepoReturnsAbsent() {
        // Setup
        when(mockMessageRepo.findMediaFor(UUID.fromString("ccc1fbb9-960c-4d88-9251-6e6799da1301")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<Media> result = messageFileServiceUnderTest.getPicFor(
                UUID.fromString("ccc1fbb9-960c-4d88-9251-6e6799da1301"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
