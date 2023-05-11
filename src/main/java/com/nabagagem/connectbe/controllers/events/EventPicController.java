package com.nabagagem.connectbe.controllers.events;

import com.nabagagem.connectbe.controllers.MediaPicControllerTrait;
import com.nabagagem.connectbe.domain.EventPicCommand;
import com.nabagagem.connectbe.services.EventPicService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events/{id}/pic")
public class EventPicController implements MediaPicControllerTrait {
    private final EventPicService eventPicService;

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable UUID id) {
        return eventPicService.getPicFor(id)
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable UUID id) {
        validateFile(file);
        eventPicService.save(new EventPicCommand(
                id,
                file));
    }
}
