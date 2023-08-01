package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.config.s3.S3Properties;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private S3Client mockS3Client;
    @Mock
    private S3Properties mockS3Properties;

    private MediaService mediaServiceUnderTest;

    @BeforeEach
    void setUp() {
        mediaServiceUnderTest = new MediaService(mockS3Client, mockS3Properties);
    }

    @Test
    void testUpload1() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        final ConnectProfile connectProfile = ConnectProfile.builder().build();
        final Media expectedResult = Media.builder()
                .id(UUID.fromString("04cacd8b-7733-46f2-8320-0d14f1986639"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .profile(ConnectProfile.builder().build())
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Run the test
        final Media result = mediaServiceUnderTest.upload(file, connectProfile);

        // Verify the results
        ArgumentCaptor<PutObjectRequest> argumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "originalName", "mediaType", "profile", "description", "fileUrl")
                .isEqualTo(expectedResult);
        verify(mockS3Client).putObject(argumentCaptor.capture(), any(RequestBody.class));
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("bucket", "bucket")
                .hasFieldOrProperty("key");
    }

    @Test
    void testUpload1_S3ClientThrowsAwsServiceException() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        final ConnectProfile connectProfile = ConnectProfile.builder().build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(AwsServiceException.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.upload(file, connectProfile))
                .isInstanceOf(AwsServiceException.class);
    }

    @Test
    void testUpload2() {
        // Setup
        ArgumentCaptor<PutObjectRequest> argumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        final Media expectedResult = Media.builder()
                .id(UUID.fromString("04cacd8b-7733-46f2-8320-0d14f1986639"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .profile(ConnectProfile.builder().build())
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Run the test
        final Media result = mediaServiceUnderTest.upload(file);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "originalName", "mediaType", "profile", "description", "fileUrl")
                .isEqualTo(expectedResult);
        verify(mockS3Client).putObject(argumentCaptor.capture(), any(RequestBody.class));
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("bucket", "bucket")
                .hasFieldOrProperty("key");
    }

    @Test
    void testDelete() {
        // Setup
        ArgumentCaptor<DeleteObjectRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        final Media media = Media.builder()
                .id(UUID.fromString("04cacd8b-7733-46f2-8320-0d14f1986639"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .profile(ConnectProfile.builder().build())
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Run the test
        mediaServiceUnderTest.delete(media);

        // Verify the results
        verify(mockS3Client).deleteObject(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("bucket", "bucket")
                .hasFieldOrPropertyWithValue("key", "fileUrl");
    }

    @Test
    void testDelete_S3ClientThrowsS3Exception() {
        // Setup
        ArgumentCaptor<DeleteObjectRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        final Media media = Media.builder()
                .id(UUID.fromString("04cacd8b-7733-46f2-8320-0d14f1986639"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .profile(ConnectProfile.builder().build())
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(S3Exception.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.delete(media)).isInstanceOf(S3Exception.class);
        verify(mockS3Client).deleteObject(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("bucket", "bucket")
                .hasFieldOrPropertyWithValue("key", "fileUrl");
    }
}
