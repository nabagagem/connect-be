package com.nabagagem.connectbe.config;

import com.nabagagem.connectbe.resources.GigResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Slf4j
@ControllerAdvice(basePackageClasses = GigResource.class)
public class ProblemHandler implements ProblemHandling {
}
