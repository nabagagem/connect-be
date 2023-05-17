package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.config.S3Properties;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class MediaService {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @SneakyThrows
    public Media upload(MultipartFile file, ConnectProfile connectProfile) {
        UUID id = UUID.randomUUID();
        return Media.builder()
                .id(id)
                .mediaType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())))
                .profile(connectProfile)
                .fileUrl(upload(file, id))
                .description(file.getOriginalFilename())
                .originalName(file.getOriginalFilename())
                .build();
    }

    @SneakyThrows
    private String upload(MultipartFile file, UUID id) {
        String key = id.toString();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getFilesBucket())
                .key(key)
                .build();
        InputStream inputStream = file.getInputStream();
        s3Client.putObject(request,
                RequestBody.fromInputStream(inputStream, inputStream.available()));
        return key;
    }

    @SneakyThrows
    public Media upload(MultipartFile file) {
        UUID id = UUID.randomUUID();
        return Media.builder()
                .id(id)
                .mediaType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())))
                .description(file.getOriginalFilename())
                .originalName(file.getOriginalFilename())
                .fileUrl(upload(file, id))
                .build();
    }

    public Optional<byte[]> readFrom(Media media) {
        return Optional.ofNullable(media.getFileUrl())
                .map(key -> {
                    String bucket = s3Properties.getFilesBucket();
                    log.info("Loading file id {} from bucket: {}", key, bucket);
                    ResponseInputStream<GetObjectResponse> s3object = s3Client.getObject(
                            GetObjectRequest.builder().bucket(bucket).key(key).build());
                    try {
                        return s3object.readAllBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void delete(Media media) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3Properties.getFilesBucket()).key(media.getFileUrl())
                .build());
    }
}
