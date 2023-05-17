package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.ProfilePicCommand;
import com.nabagagem.connectbe.services.ProfilePicService;
import com.nabagagem.connectbe.services.SlugService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/pic")
public class ProfilePicController {
    private final ProfilePicService profilePicService;
    private final SlugService slugService;
    private final MediaControllerHelper mediaControllerHelper;

    @PostAuthorize("#id == authentication.name")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CacheEvict(value = "profile-pic", key = "{#id}")
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable String id) {
        mediaControllerHelper.validateFile(file);
        profilePicService.save(new ProfilePicCommand(
                slugService.getProfileIdFrom(id),
                file));
    }

    @GetMapping
    @Cacheable(value = "profile-pic", key = "{#id}")
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        log.info("Retrieving user picture: {}", id);
        return profilePicService.getPicFor(slugService.getProfileIdFrom(id))
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
