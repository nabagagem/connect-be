package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.rating.CreateRatingCommand;
import com.nabagagem.connectbe.domain.rating.RatingPayload;
import com.nabagagem.connectbe.services.rating.RatingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
