package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.Thread;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadIndexService {
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ThreadKeywordService threadKeywordService;

    public void submitReindex(Thread thread) {
        threadPoolTaskExecutor.submit(() -> {
            log.info("Reindexing thread id: {}", thread.getId());
            threadKeywordService.reindex(thread);
        });
    }
}
