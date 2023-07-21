package com.nabagagem.connectbe.domain.bid;

import com.nabagagem.connectbe.domain.bid.BidSearchParams;

import java.util.UUID;

public record ListBidsCommand(UUID userId,
                              BidSearchParams bidSearchParams,
                              org.springframework.data.domain.Sort pageable) {
}
