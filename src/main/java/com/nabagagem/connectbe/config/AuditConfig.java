package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.services.profile.UserInfoService;
import com.nabagagem.connectbe.services.profile.UserInfoService.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean("auditorProvider")
    public AuditorAware<String> auditorProvider(UserInfoService userInfoService) {
        return () -> Optional.ofNullable(userInfoService.getCurrentUserInfo(null))
                .map(UserInfo::userId)
                .map(UUID::toString);
    }
}
