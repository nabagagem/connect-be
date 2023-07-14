package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMediaItem;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileMedia;
import com.nabagagem.connectbe.repos.ProfileMediaRepository;
import com.nabagagem.connectbe.services.MediaService;
import com.nabagagem.connectbe.services.mappers.ProfileMediaMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfileMediaService {
    private final MediaService mediaService;
    private final ProfileMediaRepository profileMediaRepository;
    private final ProfileMediaMapper profileMediaMapper;

    public ProfileMedia create(UUID profileId, MultipartFile file) {
        return profileMediaRepository.save(
                ProfileMedia.builder()
                        .profile(ConnectProfile.builder().id(profileId).build())
                        .media(mediaService.upload(file))
                        .build()
        );
    }

    public Set<ProfileMediaItem> listForProfile(UUID profileId) {
        return profileMediaRepository.getProfileMedia(profileId)
                .stream().map(profileMediaMapper::toDto)
                .collect(Collectors.toSet());
    }

    public void delete(UUID mediaId) {
        profileMediaRepository.findById(mediaId)
                .ifPresent(profileMedia -> {
                    mediaService.delete(profileMedia.getMedia());
                    profileMediaRepository.delete(profileMedia);
                });
    }
}
