package com.nabagagem.connectbe.domain.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class BadRequestException extends RuntimeException implements BusinessException {
    private final ErrorType errorType;
    private final Object[] args;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
