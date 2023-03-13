package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.entities.Account;
import com.nabagagem.connectbe.resources.AccountResource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    private final AccountResource accountResource;

    @Bean("auditorProvider")
    public AuditorAware<Account> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .map(UUID::fromString)
                .flatMap(accountResource::findById);
    }
}
