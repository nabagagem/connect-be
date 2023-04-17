package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.CONFLICT,
        reason = "You exceeded the amount of top skills")
public class SkillTopCountExceeded extends RuntimeException {
}
