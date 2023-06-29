package com.nabagagem.connectbe.domain.exceptions;

import org.springframework.http.HttpStatus;

public class MessageCannotBeRead extends RuntimeException implements BusinessException {
    @Override
    public ErrorType getErrorType() {
        return ErrorType.MESSAGE_CANNOT_BE_READ;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
