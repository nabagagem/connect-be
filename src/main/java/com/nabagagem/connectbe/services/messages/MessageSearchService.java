package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.MessageSearchParams;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageSearchService {
    private final KeywordService keywordService;
    private final MessageRepo messageRepo;

    public Page<Message> getMessagesPageFrom(UUID threadId, Pageable pageable, MessageSearchParams messageSearchParams) {
        return Optional.ofNullable(messageSearchParams.messageId())
                .map(messageId -> findByMessageId(messageSearchParams))
                .orElseGet(() -> findByExpression(messageSearchParams, threadId, pageable));
    }

    private Page<Message> findByExpression(MessageSearchParams messageSearchParams, UUID threadId, Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(messageSearchParams.expression())
                .map(keywordService::extractFrom)
                .orElseGet(Set::of);
        Page<UUID> ids = messageRepo.findMessageIdsByThread(
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

    private Page<Message> findByMessageId(MessageSearchParams messageSearchParams) {
        List<UUID> ids = messageRepo.findMessagePage(
                messageSearchParams.messageId(),
                Optional.ofNullable(messageSearchParams.behind()).orElse(10),
                Optional.ofNullable(messageSearchParams.inFront()).orElse(10));
        return new PageImpl<>(
                messageRepo.findFullPageByIds(
                        ids,
                        Sort.by(Sort.Direction.DESC, "audit.createdAt")),
                Pageable.unpaged(),
                ids.size()
        );
    }

}
