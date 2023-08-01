package com.nabagagem.connectbe.services.events;

import com.nabagagem.connectbe.domain.exceptions.EventNotFoundException;
import com.nabagagem.connectbe.domain.notification.EventPicCommand;
import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.EventRepository;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPicServiceTest {

    @Mock
    private EventRepository mockEventRepository;
    @Mock
    private MediaService mockMediaService;

    private EventPicService eventPicServiceUnderTest;

    @BeforeEach
    void setUp() {
        eventPicServiceUnderTest = new EventPicService(mockEventRepository, mockMediaService);
    }

    @Test
    void testSave() {
        // Setup
        UUID id = UUID.fromString("274925eb-da68-4af7-9d56-2748782d84bd");
        final EventPicCommand eventPicCommand = new EventPicCommand(
                id,
                new MockMultipartFile("name", "content".getBytes()));

        // Configure EventRepository.findById(...).
        Media media = Media.builder()
                .fileUrl("url")
                .mediaType(MediaType.ALL)
                .build();
        final Optional<Event> event = Optional.of(Event.builder()
                .eventPicture(media)
                .build());
        when(mockEventRepository.findById(id)).thenReturn(event);

        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(media);

        // Configure EventRepository.save(...).
        final Event event1 = Event.builder()
                .eventPicture(media)
                .build();
        when(mockEventRepository.save(Event.builder()
                .eventPicture(media)
                .build())).thenReturn(event1);

        // Run the test
        eventPicServiceUnderTest.save(eventPicCommand);

        // Verify the results
    }

    @Test
    void testSave_EventRepositoryFindByIdReturnsAbsent() {
        // Setup
        UUID id = UUID.fromString("274925eb-da68-4af7-9d56-2748782d84bd");
        final EventPicCommand eventPicCommand = new EventPicCommand(
                id,
                new MockMultipartFile("name", "content".getBytes()));
        when(mockEventRepository.findById(id))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> eventPicServiceUnderTest.save(eventPicCommand))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void testSave_EventRepositorySaveThrowsOptimisticLockingFailureException() {
        // Setup
        UUID id = UUID.fromString("274925eb-da68-4af7-9d56-2748782d84bd");
        final EventPicCommand eventPicCommand = new EventPicCommand(
                id,
                new MockMultipartFile("name", "content".getBytes()));

        // Configure EventRepository.findById(...).
        final Optional<Event> event = Optional.of(Event.builder()
                .eventPicture(Media.builder().build())
                .build());
        when(mockEventRepository.findById(id)).thenReturn(event);

        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(Media.builder().build());
        when(mockEventRepository.save(Event.builder()
                .eventPicture(Media.builder().build())
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> eventPicServiceUnderTest.save(eventPicCommand))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testGetPicFor() {
        // Setup
        final Optional<Media> expectedResult = Optional.of(Media.builder().build());
        when(mockEventRepository.findPicFor(UUID.fromString("6a35dd71-102c-4b49-b89d-4354131d1e78")))
                .thenReturn(Optional.of(Media.builder().build()));

        // Run the test
        final Optional<Media> result = eventPicServiceUnderTest.getPicFor(
                UUID.fromString("6a35dd71-102c-4b49-b89d-4354131d1e78"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetPicFor_EventRepositoryReturnsAbsent() {
        // Setup
        when(mockEventRepository.findPicFor(UUID.fromString("6a35dd71-102c-4b49-b89d-4354131d1e78")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<Media> result = eventPicServiceUnderTest.getPicFor(
                UUID.fromString("6a35dd71-102c-4b49-b89d-4354131d1e78"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
