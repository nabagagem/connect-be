package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ProfileRatingPayload;
import com.nabagagem.connectbe.services.RatingListService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{profileId}/ratings")
public class ProfileRatingController {
    private final RatingListService ratingListService;

    @GetMapping
    public Page<ProfileRatingPayload> list(@PathVariable UUID profileId,
                                           @PageableDefault(size = 5)
                                           @SortDefault(sort = "audit.createdAt", direction = Sort.Direction.DESC)
                                           Pageable pageable) {

        return ratingListService.findRatingsFor(profileId, pageable);
    }

}
