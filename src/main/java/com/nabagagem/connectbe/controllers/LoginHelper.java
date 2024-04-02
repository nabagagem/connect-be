package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.services.profile.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LoginHelper {
    private final UserInfoService userInfoService;

    public Optional<UUID> loggedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .filter(name -> !name.contains("anonymous"))
                .map(__ -> userInfoService.getCurrentUserInfo(null))
                .map(UserInfoService.UserInfo::userId);
    }

    public UUID getLoggedUserId() {
        return loggedUser().orElseThrow();
    }

}
