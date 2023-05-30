package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.CreateRatingCommand;
import com.nabagagem.connectbe.domain.RatingPayload;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.services.RatingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResourceRef post(@RequestBody @Valid RatingPayload ratingPayload,
                            Principal principal) {
        return new ResourceRef(ratingService.create(
                new CreateRatingCommand(
                        ratingPayload,
                        UUID.fromString(principal.getName())
                )
        ).getId().toString());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        ratingService.delete(id);
    }
}
