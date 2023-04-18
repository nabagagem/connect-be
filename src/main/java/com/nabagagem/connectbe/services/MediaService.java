package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MediaService {
    @SneakyThrows
    public Media toMedia(MultipartFile file, ConnectProfile connectProfile) {
        return Media.builder()
                .mediaType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())))
                .profile(connectProfile)
                .fileContent(IOUtils.toByteArray(file.getInputStream()))
                .description(file.getOriginalFilename())
                .originalName(file.getOriginalFilename())
                .build();
    }
}
