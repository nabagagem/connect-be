package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.services.profile.OnlineUserService;
import com.nabagagem.connectbe.services.profile.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserOnlineController {
    private final OnlineUserService onlineUserService;
    private final SlugService slugService;

    @GetMapping("/api/v1/profile/{id}/online")
    public OnlineStatus get(@PathVariable String id) {
        return new OnlineStatus(
                onlineUserService.isOnline(
                        slugService.getProfileIdFrom(id)));
    }

    private record OnlineStatus(Boolean online) {
    }
}
