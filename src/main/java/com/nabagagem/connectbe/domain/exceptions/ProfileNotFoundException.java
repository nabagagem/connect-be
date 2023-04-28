package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Profile not found")
public class ProfileNotFoundException extends RuntimeException {
}
