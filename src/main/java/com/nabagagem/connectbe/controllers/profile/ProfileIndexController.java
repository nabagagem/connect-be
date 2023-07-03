package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.services.profile.ReindexService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/internal/reindex")
public class ProfileIndexController {
    private final ReindexService reindexService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping
    public void reindex() {
        threadPoolTaskExecutor.submit(reindexService::reindex);
    }

}
