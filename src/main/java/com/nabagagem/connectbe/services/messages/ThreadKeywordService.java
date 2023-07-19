package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadKeywordService {
    private final KeywordService keywordService;
    private final MessageRepo messageRepo;
    private final ThreadRepo threadRepo;

    @Transactional
    public void reindex(Thread thread) {
        Message lastMessage = thread.getLastMessage();
        Set<String> keywords = messageRepo.streamByThread(thread)
                .map(Message::getText)
                .map(keywordService::extractFrom)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        keywords.addAll(keywordService.extractFrom(lastMessage.getText()));
        thread.setKeywords(
                keywords
        );
        log.info("Thread id {} reindexed with {}", thread.getId(), thread.getKeywords());
        threadRepo.save(thread);
    }
}
