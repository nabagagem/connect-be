package com.nabagagem.connectbe.domain;

import java.util.UUID;

public record ListBidsCommand(UUID userId,
                              BidSearchParams bidSearchParams,
                              org.springframework.data.domain.Sort pageable) {
}
