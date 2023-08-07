package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.repos.ProfileRepo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = ProfileRepo.class,
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class HistoryConfig {
}
