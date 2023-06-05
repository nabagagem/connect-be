package com.nabagagem.connectbe.config;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collections;

@Configuration
@AllArgsConstructor
public class MailTemplateConfig {
    private final MessageSource messageSource;

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setResolvablePatterns(Collections.singleton("*"));
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
