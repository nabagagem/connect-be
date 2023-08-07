package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.MessageSearchParams;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageSearchService {
    private final KeywordService keywordService;
    private final MessageRepo messageRepo;

    public Page<Message> getMessagesPageFrom(UUID threadId, Pageable pageable, MessageSearchParams messageSearchParams) {
        Set<String> keywords = Optional.ofNullable(messageSearchParams.expression())
                .map(keywordService::extractFrom)
                .orElseGet(Set::of);
        Page<String> ids = messageRepo.findMessageIdsByThread(
                threadId,
                keywords,
                keywords.isEmpty(),
                pageable);
        return new PageImpl<>(
                messageRepo.findFullPageByIds(ids.getContent(), pageable.getSort()),
                ids.getPageable(),
                ids.getTotalElements()
        );
    }

}
