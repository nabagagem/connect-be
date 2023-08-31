package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public class NotFound extends RuntimeException implements BusinessException {
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NOT_FOUND;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
