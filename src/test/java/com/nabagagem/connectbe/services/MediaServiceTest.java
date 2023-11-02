package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.config.s3.S3Properties;
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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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
    void testUpload() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        ArgumentCaptor<PutObjectRequest> argumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        final Media expectedResult = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Run the test
        final Media result = mediaServiceUnderTest.upload(file);

        // Verify the results
        assertThat(result)
                .hasFieldOrProperty("fileUrl");
        verify(mockS3Client).putObject(argumentCaptor.capture(), any(RequestBody.class));
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("bucket", "bucket")
                .hasFieldOrProperty("key");
    }

    @Test
    void testReadFrom() throws Exception {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Configure S3Client.getObject(...).
        final ResponseInputStream<GetObjectResponse> responseResponseInputStream =
                new ResponseInputStream<>(GetObjectResponse.builder().build(),
                        AbortableInputStream.create(new ByteArrayInputStream("objectContent".getBytes())));

        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenReturn(responseResponseInputStream);

        // Run the test
        final Optional<byte[]> result = mediaServiceUnderTest.readFrom(media);

        // Verify the results
        assertThat(result.map(String::new)).contains("objectContent");
    }

    @Test
    void testReadFrom_S3ClientReturnsNoContent() throws Exception {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Configure S3Client.getObject(...).
        final ResponseInputStream<GetObjectResponse> spyResponseInputStream =
                new ResponseInputStream<>(GetObjectResponse.builder().build(),
                        AbortableInputStream.create(InputStream.nullInputStream()));
        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenReturn(spyResponseInputStream);

        // Run the test
        final Optional<byte[]> result = mediaServiceUnderTest.readFrom(media);

        // Verify the results
        assertThat(result).contains(new byte[0]);
    }

    @Test
    void testReadFrom_S3ClientThrowsNoSuchKeyException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(NoSuchKeyException.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.readFrom(media)).isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    void testReadFrom_S3ClientThrowsInvalidObjectStateException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(InvalidObjectStateException.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.readFrom(media)).isInstanceOf(InvalidObjectStateException.class);
    }

    @Test
    void testReadFrom_S3ClientThrowsAwsServiceException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(AwsServiceException.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.readFrom(media)).isInstanceOf(AwsServiceException.class);
    }

    @Test
    void testReadFrom_S3ClientThrowsSdkClientException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(SdkClientException.class);

        // Run the test
        assertThatThrownBy(() -> mediaServiceUnderTest.readFrom(media)).isInstanceOf(SdkClientException.class);
    }

    @Test
    void testDelete() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("key")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");

        // Run the test
        mediaServiceUnderTest.delete(media);

        // Verify the results
        verify(mockS3Client).deleteObject(DeleteObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build());
    }

    @Test
    void testDelete_S3ClientThrowsAwsServiceException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(AwsServiceException.class);

        // Run the test
        mediaServiceUnderTest.delete(media);
    }

    @Test
    void testDelete_S3ClientThrowsSdkClientException() {
        // Setup
        final Media media = Media.builder()
                .id(UUID.fromString("afbfd31f-cf59-45f9-9797-8287db2f8edc"))
                .originalName("originalName")
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .fileUrl("fileUrl")
                .description("description")
                .build();
        when(mockS3Properties.getFilesBucket()).thenReturn("bucket");
        when(mockS3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket("bucket")
                .key("key")
                .build())).thenThrow(SdkClientException.class);

        // Run the test
        mediaServiceUnderTest.delete(media);
    }
}
