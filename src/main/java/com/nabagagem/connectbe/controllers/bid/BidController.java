package com.nabagagem.connectbe.controllers.bid;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.bid.BidCommand;
import com.nabagagem.connectbe.domain.bid.BidPayload;
import com.nabagagem.connectbe.domain.bid.BidSearchParams;
import com.nabagagem.connectbe.domain.bid.ListBidsCommand;
import com.nabagagem.connectbe.services.bid.BidAuthService;
import com.nabagagem.connectbe.services.bid.BidMapper;
import com.nabagagem.connectbe.services.bid.BidService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class BidController {
    private final BidService bidService;
    private final BidMapper bidMapper;
    private final BidAuthService bidAuthService;

    @PostMapping("/api/v1/bids")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceRef create(@RequestBody @Valid BidCommand bidCommand) {
        return new ResourceRef(
                bidService.create(bidCommand).getId().toString()
        );
    }

    @GetMapping("/api/v1/bids/{bidId}")
    public ResponseEntity<BidPayload> get(@PathVariable UUID bidId) {
        return bidService.findById(bidId)
                .filter(bidAuthService::isAllowedToRead)
                .map(bidMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/v1/bids/{bidId}")
    public void delete(@PathVariable UUID bidId) {
        bidAuthService.failIfUnableToDelete(bidId);
        bidService.delete(bidId);
    }

    @GetMapping("/api/v1/profile/{userId}/bids")
    public List<BidPayload> userBids(@PathVariable UUID userId,
                                     @Valid @NotNull BidSearchParams bidSearchParams,
                                     Sort sort) {
        return bidService.listUserBids(
                        new ListBidsCommand(userId, bidSearchParams, sort))
                .stream()
                .map(bidMapper::toDto)
                .collect(Collectors.toList());
    }
}
